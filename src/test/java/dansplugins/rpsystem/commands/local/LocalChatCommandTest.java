package dansplugins.rpsystem.commands.local;

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
 * Characterizes {@code /local} (alias {@code /rp}) as documented in {@code COMMANDS.md}: which permission nodes it
 * accepts, that it switches the player into local chat, and the {@code hide} / {@code show} sub-commands that toggle
 * whether incoming roleplay chat is withheld.
 *
 * <p>{@link MedievalRoleplayEngine} is a {@code JavaPlugin}, whose constructor refuses to run outside a server's
 * plugin class loader, so the plugin is allocated without running any constructor and only the fields
 * {@link LocalChatCommand} reads are filled in. The real {@link EphemeralData} is used; the alert colors are fixed.</p>
 */
public class LocalChatCommandTest {
    private static final ChatColor POSITIVE = ChatColor.GREEN;
    private static final ChatColor NEGATIVE = ChatColor.RED;
    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String DENIED = NEGATIVE + "Sorry! In order to use this command, you need one the following"
            + " permissions: 'rp.local', 'rp.rp'";

    private final EphemeralData ephemeralData = new EphemeralData();
    private final List<String> received = new ArrayList<>();
    private final Set<String> permissions = new HashSet<>();

    private LocalChatCommand localChatCommand;
    private Player player;

    @Before
    public void setUp() throws Exception {
        localChatCommand = new LocalChatCommand(plugin());
        player = player();
    }

    @Test
    public void aSenderThatIsNotAPlayerIsRefusedWithoutAMessage() {
        assertFalse(localChatCommand.startChattingInLocalChat(console(), new String[0]));
        assertFalse(localChatCommand.startChattingInLocalChat(console(), new String[]{"hide"}));

        assertTrue(ephemeralData.getPlayersSpeakingInLocalChat().isEmpty());
        assertTrue(ephemeralData.getPlayersWhoHaveHiddenLocalChat().isEmpty());
    }

    @Test
    public void localRpAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.local", "rp.rp", "rp.default")) {
            permissions.clear();
            permissions.add(node);
            ephemeralData.getPlayersSpeakingInLocalChat().clear();
            received.clear();

            assertTrue(node, localChatCommand.startChattingInLocalChat(player, new String[0]));

            assertEquals(node, Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersSpeakingInLocalChat());
            assertEquals(node, Collections.singletonList(POSITIVE + "You are now talking in local chat."), received);
        }
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndNothingChanges() {
        for (String[] args : Arrays.asList(new String[0], new String[]{"hide"}, new String[]{"show"})) {
            received.clear();

            assertFalse(localChatCommand.startChattingInLocalChat(player, args));

            assertEquals(Collections.singletonList(DENIED), received);
        }
        assertTrue(ephemeralData.getPlayersSpeakingInLocalChat().isEmpty());
        assertTrue(ephemeralData.getPlayersWhoHaveHiddenLocalChat().isEmpty());
    }

    @Test
    public void switchingWhileAlreadyInLocalChatSaysSoAndDoesNotAddThePlayerTwice() {
        permissions.add("rp.local");
        localChatCommand.startChattingInLocalChat(player, new String[0]);
        received.clear();

        assertTrue(localChatCommand.startChattingInLocalChat(player, new String[0]));

        assertEquals(Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersSpeakingInLocalChat());
        assertEquals(Collections.singletonList(POSITIVE + "You're already now in local chat."), received);
    }

    @Test
    public void messageTextAfterTheCommandIsIgnoredAndStillSwitchesChannel() {
        permissions.add("rp.local");

        assertTrue(localChatCommand.startChattingInLocalChat(player, new String[]{"hello", "there"}));

        assertEquals(Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersSpeakingInLocalChat());
        assertEquals(Collections.singletonList(POSITIVE + "You are now talking in local chat."), received);
    }

    @Test
    public void hideWithholdsLocalChatWithoutSwitchingChannel() {
        permissions.add("rp.local");

        assertTrue(localChatCommand.startChattingInLocalChat(player, new String[]{"hide"}));

        assertEquals(Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersWhoHaveHiddenLocalChat());
        assertTrue(ephemeralData.getPlayersSpeakingInLocalChat().isEmpty());
        assertEquals(Collections.singletonList(POSITIVE + "Local chat is now hidden!"), received);
    }

    @Test
    public void hidingTwiceSaysItIsAlreadyHidden() {
        permissions.add("rp.local");
        localChatCommand.startChattingInLocalChat(player, new String[]{"hide"});
        received.clear();

        assertTrue(localChatCommand.startChattingInLocalChat(player, new String[]{"hide"}));

        assertEquals(Collections.singletonList(PLAYER_UUID), ephemeralData.getPlayersWhoHaveHiddenLocalChat());
        assertEquals(Collections.singletonList(NEGATIVE + "Local chat is already hidden!"), received);
    }

    @Test
    public void showMakesHiddenLocalChatVisibleAgain() {
        permissions.add("rp.local");
        localChatCommand.startChattingInLocalChat(player, new String[]{"hide"});
        received.clear();

        assertTrue(localChatCommand.startChattingInLocalChat(player, new String[]{"show"}));

        assertTrue(ephemeralData.getPlayersWhoHaveHiddenLocalChat().isEmpty());
        assertTrue(ephemeralData.getPlayersSpeakingInLocalChat().isEmpty());
        assertEquals(Collections.singletonList(POSITIVE + "Local chat is now visible!"), received);
    }

    @Test
    public void showWhenNotHiddenSaysItIsAlreadyVisible() {
        permissions.add("rp.local");

        assertTrue(localChatCommand.startChattingInLocalChat(player, new String[]{"show"}));

        assertTrue(ephemeralData.getPlayersWhoHaveHiddenLocalChat().isEmpty());
        assertEquals(Collections.singletonList(NEGATIVE + "Local chat is already visible!"), received);
    }

    @Test
    public void hideAndShowIgnoreCaseAndAnyWordsAfterThem() {
        permissions.add("rp.local");

        localChatCommand.startChattingInLocalChat(player, new String[]{"HIDE", "please"});
        localChatCommand.startChattingInLocalChat(player, new String[]{"Show", "again"});

        assertTrue(ephemeralData.getPlayersWhoHaveHiddenLocalChat().isEmpty());
        assertTrue(ephemeralData.getPlayersSpeakingInLocalChat().isEmpty());
        assertEquals(Arrays.asList(
                POSITIVE + "Local chat is now hidden!",
                POSITIVE + "Local chat is now visible!"), received);
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
