package dansplugins.rpsystem.utils;

import org.bukkit.ChatColor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Characterizes how the color names in {@code config.yml} ({@code localChatColor}, {@code emoteColor}, the alert
 * colors and the rest documented in {@code CONFIG.md}) are resolved to a {@link ChatColor}.
 * {@link ColorChecker#getColorByName} does not consult the plugin, so no plugin instance is needed.
 */
public class ColorCheckerTest {
    private static final ChatColor[] SUPPORTED_COLORS = {
            ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE,
            ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.GOLD,
            ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.RED, ChatColor.YELLOW, ChatColor.WHITE
    };

    private final ColorChecker colorChecker = new ColorChecker(null);

    @Test
    public void everyColorIsResolvedFromItsLowerCaseName() {
        for (ChatColor color : SUPPORTED_COLORS) {
            assertEquals(color, colorChecker.getColorByName(color.name().toLowerCase()));
        }
    }

    @Test
    public void everyColorIsResolvedFromItsUpperCaseName() {
        for (ChatColor color : SUPPORTED_COLORS) {
            assertEquals(color, colorChecker.getColorByName(color.name()));
        }
    }

    @Test
    public void mixedCaseNamesAreNotRecognizedAndFallBackToWhite() {
        // Only the all-lower-case and all-upper-case spellings are matched.
        assertEquals(ChatColor.WHITE, colorChecker.getColorByName("Red"));
        assertEquals(ChatColor.WHITE, colorChecker.getColorByName("Dark_Red"));
    }

    @Test
    public void unknownNamesFallBackToWhite() {
        assertEquals(ChatColor.WHITE, colorChecker.getColorByName("pink"));
        assertEquals(ChatColor.WHITE, colorChecker.getColorByName("dark-red"));
        assertEquals(ChatColor.WHITE, colorChecker.getColorByName(""));
    }

    @Test
    public void formattingCodesAreNotColorsAndFallBackToWhite() {
        assertEquals(ChatColor.WHITE, colorChecker.getColorByName("bold"));
        assertEquals(ChatColor.WHITE, colorChecker.getColorByName("ITALIC"));
    }

    @Test(expected = NullPointerException.class)
    public void aNullNameThrows() {
        // A switch on a null String throws. ConfigService backfills every color option on enable, so callers are
        // not expected to pass null; this records the behavior rather than endorsing it.
        colorChecker.getColorByName(null);
    }
}
