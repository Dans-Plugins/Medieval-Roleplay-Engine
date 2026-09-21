package dansplugins.rpsystem;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Exercises the {@code rp.card.*} registration in {@code plugin.yml} through Bukkit's own permission
 * resolution ({@link PermissibleBase} + {@link PermissionAttachment}), so the guarantee is the one a
 * live server gives rather than the one the YAML appears to give.
 *
 * <p>This is the check PR #335 established with a throwaway class: with {@code rp.card.*} unregistered,
 * granting it did not restore a card node the server had revoked; with it registered as a parent, it does.</p>
 */
public class CardWildcardPermissionTest {
    private static final String[] PLAYER_CARD_NODES = {
            "rp.card.show", "rp.card.lookup", "rp.card.help", "rp.card.name", "rp.card.race",
            "rp.card.subculture", "rp.card.religion", "rp.card.age", "rp.card.gender"
    };

    private static Plugin plugin;

    private boolean op;
    private PermissibleBase permissible;

    @BeforeClass
    public static void registerPluginYmlPermissions() {
        BukkitTestServer.installWithPluginYmlPermissions();
        plugin = BukkitTestServer.enabledPlugin();
    }

    @Before
    public void createNonOperatorPermissible() {
        op = false;
        permissible = new PermissibleBase(new ServerOperator() {
            @Override
            public boolean isOp() {
                return op;
            }

            @Override
            public void setOp(boolean value) {
                op = value;
            }
        });
    }

    @Test
    public void playerCardNodesAreGrantedByDefaultAndTheWildcardIsNot() {
        for (String node : PLAYER_CARD_NODES) {
            assertTrue(node + " has default: true in plugin.yml", permissible.hasPermission(node));
        }
        assertFalse("rp.card.* has default: op in plugin.yml", permissible.hasPermission("rp.card.*"));
        assertFalse(permissible.hasPermission("rp.card.forcesave"));
        assertFalse(permissible.hasPermission("rp.card.forceload"));
    }

    @Test
    public void operatorsHoldTheWildcardAndTheAdminCardNodes() {
        permissible.setOp(true);
        assertTrue(permissible.hasPermission("rp.card.*"));
        assertTrue(permissible.hasPermission("rp.card.forcesave"));
        assertTrue(permissible.hasPermission("rp.card.forceload"));
    }

    @Test
    public void grantingTheWildcardRestoresARevokedCardNode() {
        // The server revokes one card node, then grants the wildcard, in that order: Bukkit applies attachments
        // in insertion order, so the later grant only wins if rp.card.* actually parents rp.card.name.
        permissible.addAttachment(plugin).setPermission("rp.card.name", false);
        assertFalse("precondition: the revoke took effect", permissible.hasPermission("rp.card.name"));

        permissible.addAttachment(plugin).setPermission("rp.card.*", true);

        assertTrue("rp.card.* must re-grant rp.card.name through its children", permissible.hasPermission("rp.card.name"));
        for (String node : PLAYER_CARD_NODES) {
            assertTrue(node + " must be held while rp.card.* is granted", permissible.hasPermission(node));
        }
    }

    @Test
    public void revokingTheWildcardRevokesEveryPlayerCardNode() {
        permissible.addAttachment(plugin).setPermission("rp.card.*", false);

        for (String node : PLAYER_CARD_NODES) {
            assertFalse(node + " must be revoked while rp.card.* is revoked", permissible.hasPermission(node));
        }
    }

    @Test
    public void theWildcardDoesNotReachTheAdminCardNodes() {
        permissible.addAttachment(plugin).setPermission("rp.card.*", true);

        assertFalse("rp.card.forcesave is not a child of rp.card.*", permissible.hasPermission("rp.card.forcesave"));
        assertFalse("rp.card.forceload is not a child of rp.card.*", permissible.hasPermission("rp.card.forceload"));
    }
}
