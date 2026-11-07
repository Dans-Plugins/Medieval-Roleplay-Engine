package dansplugins.rpsystem.commands.whisper;

import dansplugins.rpsystem.commands.ChatCommandFixture;
import org.bukkit.ChatColor;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static dansplugins.rpsystem.commands.ChatCommandFixture.CHARACTER;
import static dansplugins.rpsystem.commands.ChatCommandFixture.NEGATIVE;
import static dansplugins.rpsystem.commands.ChatCommandFixture.NEUTRAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes {@code /whisper} as documented in {@code COMMANDS.md}: which permission nodes it accepts, how a message
 * is sent, and what the sender is told afterwards.
 */
public class WhisperCommandTest {
    private static final int WHISPER_RADIUS = 7;
    private static final String[] MESSAGE = {"Meet", "me", "at", "dawn"};

    private ChatCommandFixture fixture;
    private WhisperCommand whisperCommand;

    @Before
    public void setUp() throws Exception {
        fixture = new ChatCommandFixture();
        fixture.config.set("whisperChatRadius", WHISPER_RADIUS);
        fixture.config.set("whisperChatColor", "dark_aqua");
        whisperCommand = new WhisperCommand(fixture.plugin);
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnored() {
        whisperCommand.sendQuietMessage(ChatCommandFixture.console(), MESSAGE);

        assertTrue(fixture.messenger.roleplay.isEmpty());
    }

    @Test
    public void whisperAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.whisper", "rp.default")) {
            fixture.permissions.clear();
            fixture.permissions.add(node);
            fixture.messenger.roleplay.clear();

            whisperCommand.sendQuietMessage(fixture.player, MESSAGE);

            assertEquals(node, 1, fixture.messenger.roleplay.size());
        }
    }

    @Test
    public void theMessageIsSentUnderTheCharacterNameWithinTheWhisperRadius() {
        fixture.permissions.add("rp.whisper");

        whisperCommand.sendQuietMessage(fixture.player, MESSAGE);

        assertEquals(1, fixture.messenger.roleplay.size());
        ChatCommandFixture.Broadcast broadcast = fixture.messenger.roleplay.get(0);
        assertEquals(ChatColor.DARK_AQUA + CHARACTER + " whispers: \"Meet me at dawn\"", broadcast.message);
        assertEquals(WHISPER_RADIUS, broadcast.distance);
        assertSame(fixture.player, broadcast.sender);
        assertTrue(fixture.messenger.outOfCharacter.isEmpty());
    }

    @Test
    public void theSenderIsNotCountedAmongThePlayersWhoHeardIt() {
        fixture.permissions.add("rp.whisper");
        fixture.messenger.recipientCount = 3;

        whisperCommand.sendQuietMessage(fixture.player, MESSAGE);

        assertEquals(Collections.singletonList(NEUTRAL + "2 players heard your whisper."), fixture.received);
    }

    @Test
    public void aSenderWithNobodyNearbyIsToldNoPlayersHeardIt() {
        fixture.permissions.add("rp.whisper");
        fixture.messenger.recipientCount = 1;

        whisperCommand.sendQuietMessage(fixture.player, MESSAGE);

        assertEquals(Collections.singletonList(NEUTRAL + "0 players heard your whisper."), fixture.received);
    }

    @Test
    public void oneOtherPlayerIsWordedInTheSingular() {
        fixture.permissions.add("rp.whisper");
        fixture.messenger.recipientCount = 2;

        whisperCommand.sendQuietMessage(fixture.player, MESSAGE);

        assertEquals(Collections.singletonList(NEUTRAL + "1 player heard your whisper."), fixture.received);
    }

    @Test
    public void aSenderWhoHasHiddenLocalChatIsNotSubtractedBecauseTheyWereNotARecipient() {
        fixture.permissions.add("rp.whisper");
        fixture.ephemeralData.getPlayersWhoHaveHiddenLocalChat().add(ChatCommandFixture.PLAYER_UUID);
        fixture.messenger.recipientCount = 2;

        whisperCommand.sendQuietMessage(fixture.player, MESSAGE);

        assertEquals(Collections.singletonList(NEUTRAL + "2 players heard your whisper."), fixture.received);
    }

    @Test
    public void aMessageThatReachedNobodyDoesNotGoBelowZero() {
        fixture.permissions.add("rp.whisper");
        fixture.messenger.recipientCount = 0;

        whisperCommand.sendQuietMessage(fixture.player, MESSAGE);

        assertEquals(Collections.singletonList(NEUTRAL + "0 players heard your whisper."), fixture.received);
    }

    @Test
    public void withoutAMessageTheUsageIsShownAndNothingIsSent() {
        fixture.permissions.add("rp.whisper");

        whisperCommand.sendQuietMessage(fixture.player, new String[0]);

        assertEquals(Collections.singletonList(NEGATIVE + "Usage: /whisper (message)"), fixture.received);
        assertTrue(fixture.messenger.roleplay.isEmpty());
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndNothingIsSent() {
        whisperCommand.sendQuietMessage(fixture.player, MESSAGE);

        assertEquals(Collections.singletonList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission: 'rp.whisper'"),
                fixture.received);
        assertTrue(fixture.messenger.roleplay.isEmpty());
    }

    @Test
    public void permissionIsCheckedBeforeTheUsage() {
        whisperCommand.sendQuietMessage(fixture.player, new String[0]);

        assertEquals(Collections.singletonList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission: 'rp.whisper'"),
                fixture.received);
    }
}
