package dansplugins.rpsystem.commands.global;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.ephemeral.EphemeralData;
import dansplugins.rpsystem.utils.ColorChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes {@code /global} (alias {@code /ooc}) as documented in {@code COMMANDS.md}: which permission nodes it
 * accepts and that it switches the player back out of local chat, leaving any hidden-chat setting alone.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only the fields
 * {@link GlobalChatCommand} reads are filled in. The real {@link EphemeralData} is used; the alert colors are
 * fixed.</p>
 */
public class GlobalChatCommandTest {
    private static final ChatColor POSITIVE = ChatColor.GREEN;
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OTHER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final EphemeralData ephemeralData = new EphemeralData();
    private final List<String> received = new ArrayList<>();
    private final Set<String> permissions = new HashSet<>();

    private GlobalChatCommand globalChatCommand;
    private Player player;

    @Before
    public void setUp() throws Exception {
        globalChatCommand = new GlobalChatCommand(plugin());
        player = player();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsRefusedWithoutAMessage() {
        ephemeralData.getPlayersSpeakingInLocalChat().add(PLAYER_UUID);

        assertFalse(globalChatCommand.startChattingInGlobalChat(console(), new String[0]));

        assertEquals(Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersSpeakingInLocalChat());
    }

    @Test
    public void globalOocAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.global", "rp.ooc", "rp.default")) {
            permissions.clear();
            permissions.add(node);
            ephemeralData.getPlayersSpeakingInLocalChat().clear();
            ephemeralData.getPlayersSpeakingInLocalChat().add(PLAYER_UUID);
            received.clear();

            assertTrue(node, globalChatCommand.startChattingInGlobalChat(player, new String[0]));

            assertTrue(node, ephemeralData.getPlayersSpeakingInLocalChat().isEmpty());
            assertEquals(node, Collections.singletonList(POSITIVE + "You are now talking in global chat."), received);
        }
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndStaysInLocalChat() {
        ephemeralData.getPlayersSpeakingInLocalChat().add(PLAYER_UUID);

        assertTrue(globalChatCommand.startChattingInGlobalChat(player, new String[0]));

        assertEquals(Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersSpeakingInLocalChat());
        assertEquals(Collections.singletonList(NEGATIVE + "Sorry! In order to use this command, you need one of the"
                + " following permissions: 'rp.global', 'rp.ooc'"), received);
    }

    @Test
    public void switchingWhileAlreadyInGlobalChatSaysSoButStillSucceeds() {
        permissions.add("rp.global");

        assertTrue(globalChatCommand.startChattingInGlobalChat(player, new String[0]));

        assertTrue(ephemeralData.getPlayersSpeakingInLocalChat().isEmpty());
        assertEquals(Collections.singletonList(NEGATIVE + "You're already talking in global chat!"), received);
    }

    @Test
    public void onlyTheSendingPlayerLeavesLocalChat() {
        permissions.add("rp.ooc");
        ephemeralData.getPlayersSpeakingInLocalChat().add(OTHER_UUID);
        ephemeralData.getPlayersSpeakingInLocalChat().add(PLAYER_UUID);

        globalChatCommand.startChattingInGlobalChat(player, new String[0]);

        assertEquals(Collections.singletonList(OTHER_UUID), ephemeralData.getPlayersSpeakingInLocalChat());
    }

    @Test
    public void messageTextAndHideOrShowAfterTheCommandAreIgnored() {
        permissions.add("rp.global");
        ephemeralData.getPlayersSpeakingInLocalChat().add(PLAYER_UUID);
        ephemeralData.getPlayersWhoHaveHiddenLocalChat().add(PLAYER_UUID);

        assertTrue(globalChatCommand.startChattingInGlobalChat(player, new String[]{"show", "me", "everything"}));

        assertTrue(ephemeralData.getPlayersSpeakingInLocalChat().isEmpty());
        assertEquals(Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersWhoHaveHiddenLocalChat());
        assertTrue(ephemeralData.getPlayersWhoHaveHiddenGlobalChat().isEmpty());
        assertEquals(Collections.singletonList(POSITIVE + "You are now talking in global chat."), received);
    }

    private Player player() {
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        switch (method.getName()) {
                            case "hasPermission":
                                return permissions.contains((String) args[0]);
                            case "getUniqueId":
                                return PLAYER_UUID;
                            case "sendMessage":
                                received.add((String) args[0]);
                                return null;
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return "Aldric";
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
    private MedievalRoleplayEngine plugin() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        MedievalRoleplayEngine plugin =
                (MedievalRoleplayEngine) allocateInstance.invoke(unsafe, MedievalRoleplayEngine.class);
        setField(plugin, "ephemeralData", ephemeralData);
        setField(plugin, "colorChecker", new FixedColorChecker());
        return plugin;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = MedievalRoleplayEngine.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
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
