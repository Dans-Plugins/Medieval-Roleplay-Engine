package dansplugins.rpsystem.commands.title;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.utils.ArgumentParser;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Covers {@code /title} as documented in {@code COMMANDS.md}: who may rename a book, what must be held, what the
 * new name is, and what the player is told.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only the fields
 * {@link TitleCommand} reads are filled in. A real {@link ItemStack} asks the server's item factory for its meta, so
 * the held item is a {@link HeldItem} that hands out a recording {@link ItemMeta} instead.</p>
 */
public class TitleCommandTest {
    private static final ChatColor POSITIVE = ChatColor.GREEN;
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final String ADDED = POSITIVE + "Title added to book!";
    private static final String[] TITLE = {"The", "Chronicle", "of", "Aldric"};

    private final Set<String> permissions = new HashSet<>(Collections.singletonList("rp.title"));
    private final List<String> received = new ArrayList<>();
    private final List<String> displayNamesSet = new ArrayList<>();
    private final List<ItemStack> mainHandSetTo = new ArrayList<>();

    private TitleCommand titleCommand;
    private HeldItem held;
    private Player player;

    @Before
    public void setUp() throws Exception {
        titleCommand = new TitleCommand(plugin());
        held = new HeldItem(Material.WRITABLE_BOOK);
        player = player();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnored() {
        titleCommand.titleBook(console(), TITLE);

        assertEquals(0, held.metaSets);
    }

    @Test
    public void titleAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.title", "rp.default")) {
            permissions.clear();
            permissions.add(node);
            received.clear();

            titleCommand.titleBook(player, TITLE);

            assertEquals(node, Collections.singletonList(ADDED), received);
        }
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndTheBookIsUnchanged() {
        permissions.clear();

        titleCommand.titleBook(player, TITLE);

        assertEquals(Collections.singletonList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission: 'rp.title'"),
                received);
        assertBookUnchanged();
    }

    @Test
    public void thePermissionIsCheckedBeforeTheHeldItem() {
        permissions.clear();
        held = new HeldItem(Material.WRITTEN_BOOK);

        titleCommand.titleBook(player, new String[0]);

        assertEquals(Collections.singletonList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission: 'rp.title'"),
                received);
    }

    @Test
    public void anythingOtherThanABookAndQuillIsRefused() {
        for (Material material : Arrays.asList(Material.WRITTEN_BOOK, Material.BOOK, Material.AIR)) {
            held = new HeldItem(material);
            received.clear();

            titleCommand.titleBook(player, TITLE);

            assertEquals(material.name(), Collections.singletonList(
                    NEGATIVE + "You have to be holding a book and quill to use this command!"), received);
            assertBookUnchanged();
        }
    }

    @Test
    public void theHeldItemIsCheckedBeforeTheUsage() {
        held = new HeldItem(Material.BOOK);

        titleCommand.titleBook(player, new String[0]);

        assertEquals(Collections.singletonList(
                NEGATIVE + "You have to be holding a book and quill to use this command!"), received);
    }

    @Test
    public void noTitleShowsTheUsage() {
        titleCommand.titleBook(player, new String[0]);

        assertEquals(Collections.singletonList(NEGATIVE + "Usage: /title (new title)"), received);
        assertBookUnchanged();
    }

    @Test
    public void everyArgumentJoinedBySpacesBecomesTheDisplayName() {
        titleCommand.titleBook(player, TITLE);

        assertEquals(Collections.singletonList("The Chronicle of Aldric"), displayNamesSet);
        assertEquals(1, held.metaSets);
        assertEquals(Collections.<ItemStack>singletonList(held), mainHandSetTo);
        assertEquals(Collections.singletonList(ADDED), received);
    }

    @Test
    public void colorCodesInTheTitleAreKeptAsTyped() {
        titleCommand.titleBook(player, new String[]{"&6Gold", "Ledger"});

        assertEquals(Collections.singletonList("&6Gold Ledger"), displayNamesSet);
    }

    private void assertBookUnchanged() {
        assertEquals(0, held.metaSets);
        assertEquals(Collections.<String>emptyList(), displayNamesSet);
        assertEquals(Collections.<ItemStack>emptyList(), mainHandSetTo);
    }

    private Player player() {
        final PlayerInventory inventory = (PlayerInventory) Proxy.newProxyInstance(
                PlayerInventory.class.getClassLoader(), new Class<?>[]{PlayerInventory.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "getItemInMainHand":
                                return held;
                            case "setItemInMainHand":
                                mainHandSetTo.add((ItemStack) args[0]);
                                return null;
                            default:
                                throw new AssertionError("Unexpected call to PlayerInventory." + method.getName());
                        }
                    }
                });
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "hasPermission":
                                return permissions.contains((String) args[0]);
                            case "getInventory":
                                return inventory;
                            case "sendMessage":
                                received.add((String) args[0]);
                                return null;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            default:
                                throw new AssertionError("Unexpected call to Player." + method.getName());
                        }
                    }
                });
    }

    private static CommandSender console() {
        return (CommandSender) Proxy.newProxyInstance(CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        throw new AssertionError("The console sender should not be consulted: " + method.getName());
                    }
                });
    }

    /** Allocates the plugin without running {@code JavaPlugin}'s constructor, then sets what the command reads. */
    private static MedievalRoleplayEngine plugin() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(plugin, "colorChecker", new FixedColorChecker());
        setField(plugin, "argumentParser", new ArgumentParser());
        return plugin;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = MedievalRoleplayEngine.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * An item of a fixed type whose meta records {@code setDisplayName}, so no server item factory is needed. The
     * meta it hands back must be the one it handed out.
     */
    private final class HeldItem extends ItemStack {
        private final Material material;
        private final ItemMeta meta;
        private int metaSets;

        HeldItem(Material material) {
            super();
            this.material = material;
            this.meta = (ItemMeta) Proxy.newProxyInstance(ItemMeta.class.getClassLoader(),
                    new Class<?>[]{ItemMeta.class}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) {
                            if (method.getName().equals("setDisplayName")) {
                                displayNamesSet.add((String) args[0]);
                                return null;
                            }
                            throw new AssertionError("Unexpected call to ItemMeta." + method.getName());
                        }
                    });
        }

        @Override
        public Material getType() {
            return material;
        }

        @Override
        public ItemMeta getItemMeta() {
            return meta;
        }

        @Override
        public boolean setItemMeta(ItemMeta itemMeta) {
            assertSame(meta, itemMeta);
            metaSets++;
            return true;
        }

        @Override
        public boolean equals(Object other) {
            return this == other;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    /** Fixed alert colors, so messages can be compared without a {@code config.yml}. */
    private static final class FixedColorChecker extends ColorChecker {
        FixedColorChecker() {
            super(null);
        }

        @Override
        public ChatColor getPositiveAlertColor() {
            return POSITIVE;
        }

        @Override
        public ChatColor getNegativeAlertColor() {
            return NEGATIVE;
        }
    }
}
