package dansplugins.rpsystem.commands.localooc;

import dansplugins.rpsystem.commands.ChatCommandFixture;
import org.bukkit.ChatColor;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static dansplugins.rpsystem.commands.ChatCommandFixture.CHARACTER;
import static dansplugins.rpsystem.commands.ChatCommandFixture.NEGATIVE;
import static dansplugins.rpsystem.commands.ChatCommandFixture.PLAYER_UUID;
import static dansplugins.rpsystem.commands.ChatCommandFixture.POSITIVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes {@code /lo} as documented in {@code COMMANDS.md}: which permission nodes it accepts, how a message is
 * sent, and the {@code hide} / {@code show} sub-commands, which only count when typed on their own.
 */
public class LocalOOCChatCommandTest {
    private static final int LOCAL_OOC_RADIUS = 20;
    private static final String[] MESSAGE = {"brb", "one", "sec"};
    private static final String DENIED = NEGATIVE
            + "Sorry! In order to use this command, you need the following permission: 'rp.localOOC'";
    private static final String NOW_HIDDEN = POSITIVE + "Local OOC Chat is now hidden!";
    private static final String NOW_VISIBLE = POSITIVE + "Local OOC Chat is now visible!";

    private ChatCommandFixture fixture;
    private LocalOOCChatCommand localOOCChatCommand;

    @Before
    public void setUp() throws Exception {
        fixture = new ChatCommandFixture();
        fixture.config.set("localOOCChatRadius", LOCAL_OOC_RADIUS);
        fixture.config.set("localOOCChatColor", "dark_gray");
        fixture.permissions.add("rp.localOOC");
        localOOCChatCommand = new LocalOOCChatCommand(fixture.plugin);
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnored() {
        localOOCChatCommand.sendLocalOOCMessage(ChatCommandFixture.console(), MESSAGE);
        localOOCChatCommand.sendLocalOOCMessage(ChatCommandFixture.console(), new String[]{"hide"});

        assertTrue(fixture.messenger.outOfCharacter.isEmpty());
        assertTrue(fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().isEmpty());
    }

    @Test
    public void localOOCAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.localOOC", "rp.default")) {
            fixture.permissions.clear();
            fixture.permissions.add(node);
            fixture.messenger.outOfCharacter.clear();

            localOOCChatCommand.sendLocalOOCMessage(fixture.player, MESSAGE);

            assertEquals(node, 1, fixture.messenger.outOfCharacter.size());
        }
    }

    @Test
    public void theMessageIsSentOutOfCharacterWithinTheLocalOOCRadius() {
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, MESSAGE);

        assertEquals(1, fixture.messenger.outOfCharacter.size());
        ChatCommandFixture.Broadcast broadcast = fixture.messenger.outOfCharacter.get(0);
        assertEquals(ChatColor.DARK_GRAY + "<" + CHARACTER + "> (( brb one sec ))", broadcast.message);
        assertEquals(LOCAL_OOC_RADIUS, broadcast.distance);
        assertSame(fixture.player, broadcast.sender);
        assertTrue(fixture.messenger.roleplay.isEmpty());
        assertTrue(fixture.received.isEmpty());
    }

    @Test
    public void withoutAMessageTheUsageIsShownAndNothingIsSent() {
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[0]);

        assertEquals(Collections.singletonList(NEGATIVE + "Usage: /lo (message)"), fixture.received);
        assertTrue(fixture.messenger.outOfCharacter.isEmpty());
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndNothingIsSent() {
        fixture.permissions.clear();

        localOOCChatCommand.sendLocalOOCMessage(fixture.player, MESSAGE);

        assertEquals(Collections.singletonList(DENIED), fixture.received);
        assertTrue(fixture.messenger.outOfCharacter.isEmpty());
    }

    @Test
    public void permissionIsCheckedBeforeTheUsageAndTheSubCommands() {
        fixture.permissions.clear();

        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[0]);
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"hide"});

        assertEquals(Arrays.asList(DENIED, DENIED), fixture.received);
        assertTrue(fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().isEmpty());
    }

    @Test
    public void hideHidesLocalOOCChatAndSendsNothing() {
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"hide"});

        assertEquals(Collections.singletonList(NOW_HIDDEN), fixture.received);
        assertEquals(Collections.singletonList(PLAYER_UUID),
                fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat());
        assertTrue(fixture.ephemeralData.getPlayersWhoHaveHiddenLocalChat().isEmpty());
        assertTrue(fixture.messenger.outOfCharacter.isEmpty());
    }

    @Test
    public void hidingTwiceSaysItIsAlreadyHiddenAndChangesNothing() {
        fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().add(PLAYER_UUID);

        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"hide"});

        assertEquals(Collections.singletonList(NEGATIVE + "Local OOC Chat is already hidden!"), fixture.received);
        assertEquals(Collections.singletonList(PLAYER_UUID),
                fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat());
    }

    @Test
    public void showMakesLocalOOCChatVisibleAgainAndSendsNothing() {
        fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().add(PLAYER_UUID);

        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"show"});

        assertEquals(Collections.singletonList(NOW_VISIBLE), fixture.received);
        assertTrue(fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().isEmpty());
        assertTrue(fixture.messenger.outOfCharacter.isEmpty());
    }

    @Test
    public void showingWhenVisibleSaysItIsAlreadyVisible() {
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"show"});

        assertEquals(Collections.singletonList(NEGATIVE + "Local OOC Chat is already visible!"), fixture.received);
        assertTrue(fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().isEmpty());
    }

    @Test
    public void theSubCommandsMatchRegardlessOfCase() {
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"HIDE"});
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"Show"});

        assertEquals(Arrays.asList(NOW_HIDDEN, NOW_VISIBLE), fixture.received);
        assertTrue(fixture.messenger.outOfCharacter.isEmpty());
    }

    @Test
    public void aLongerMessageStartingWithASubCommandIsSentAsAMessage() {
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"hide", "the", "treasure"});
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"show", "me"});

        assertEquals(2, fixture.messenger.outOfCharacter.size());
        assertEquals(ChatColor.DARK_GRAY + "<" + CHARACTER + "> (( hide the treasure ))",
                fixture.messenger.outOfCharacter.get(0).message);
        assertEquals(ChatColor.DARK_GRAY + "<" + CHARACTER + "> (( show me ))",
                fixture.messenger.outOfCharacter.get(1).message);
        assertTrue(fixture.received.isEmpty());
        assertTrue(fixture.ephemeralData.getPlayersWhoHaveHiddenLocalOOCChat().isEmpty());
    }

    @Test
    public void aSingleWordThatIsNotASubCommandIsSentAsAMessage() {
        localOOCChatCommand.sendLocalOOCMessage(fixture.player, new String[]{"brb"});

        assertEquals(1, fixture.messenger.outOfCharacter.size());
        assertEquals(ChatColor.DARK_GRAY + "<" + CHARACTER + "> (( brb ))",
                fixture.messenger.outOfCharacter.get(0).message);
    }
}
