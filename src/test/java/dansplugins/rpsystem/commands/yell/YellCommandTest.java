package dansplugins.rpsystem.commands.yell;

import dansplugins.rpsystem.commands.ChatCommandFixture;
import org.bukkit.ChatColor;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static dansplugins.rpsystem.commands.ChatCommandFixture.CHARACTER;
import static dansplugins.rpsystem.commands.ChatCommandFixture.NEGATIVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes {@code /yell} as documented in {@code COMMANDS.md}: which permission nodes it accepts and how a message
 * is sent. Unlike {@code /whisper}, the sender is not told how many players heard it.
 */
public class YellCommandTest {
    private static final int YELL_RADIUS = 75;
    private static final String[] MESSAGE = {"Guards!", "Intruder!"};

    private ChatCommandFixture fixture;
    private YellCommand yellCommand;

    @Before
    public void setUp() throws Exception {
        fixture = new ChatCommandFixture();
        fixture.config.set("yellChatRadius", YELL_RADIUS);
        fixture.config.set("yellChatColor", "dark_red");
        yellCommand = new YellCommand(fixture.plugin);
    }

    @Test
    public void aSenderThatIsNotAPlayerIsIgnored() {
        yellCommand.sendLoudMessage(ChatCommandFixture.console(), MESSAGE);

        assertTrue(fixture.messenger.roleplay.isEmpty());
    }

    @Test
    public void yellAndDefaultEachGrantTheCommand() {
        for (String node : Arrays.asList("rp.yell", "rp.default")) {
            fixture.permissions.clear();
            fixture.permissions.add(node);
            fixture.messenger.roleplay.clear();

            yellCommand.sendLoudMessage(fixture.player, MESSAGE);

            assertEquals(node, 1, fixture.messenger.roleplay.size());
        }
    }

    @Test
    public void theMessageIsSentUnderTheCharacterNameWithinTheYellRadius() {
        fixture.permissions.add("rp.yell");

        yellCommand.sendLoudMessage(fixture.player, MESSAGE);

        assertEquals(1, fixture.messenger.roleplay.size());
        ChatCommandFixture.Broadcast broadcast = fixture.messenger.roleplay.get(0);
        assertEquals(ChatColor.DARK_RED + CHARACTER + " yells: \"Guards! Intruder!\"", broadcast.message);
        assertEquals(YELL_RADIUS, broadcast.distance);
        assertSame(fixture.player, broadcast.sender);
        assertTrue(fixture.messenger.outOfCharacter.isEmpty());
    }

    @Test
    public void theSenderIsToldNothingAfterASuccessfulYell() {
        fixture.permissions.add("rp.yell");
        fixture.messenger.recipientCount = 3;

        yellCommand.sendLoudMessage(fixture.player, MESSAGE);

        assertTrue(fixture.received.isEmpty());
    }

    @Test
    public void withoutAMessageTheUsageIsShownAndNothingIsSent() {
        fixture.permissions.add("rp.yell");

        yellCommand.sendLoudMessage(fixture.player, new String[0]);

        assertEquals(Collections.singletonList(NEGATIVE + "Usage: /yell (message)"), fixture.received);
        assertTrue(fixture.messenger.roleplay.isEmpty());
    }

    @Test
    public void withoutPermissionThePlayerIsToldAndNothingIsSent() {
        yellCommand.sendLoudMessage(fixture.player, MESSAGE);

        assertEquals(Collections.singletonList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission: 'rp.yell'"),
                fixture.received);
        assertTrue(fixture.messenger.roleplay.isEmpty());
    }

    @Test
    public void permissionIsCheckedBeforeTheUsage() {
        yellCommand.sendLoudMessage(fixture.player, new String[0]);

        assertEquals(Collections.singletonList(
                NEGATIVE + "Sorry! In order to use this command, you need the following permission: 'rp.yell'"),
                fixture.received);
    }
}
