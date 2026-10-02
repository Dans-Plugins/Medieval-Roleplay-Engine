package dansplugins.rpsystem.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Characterizes the two ways command arguments are joined back into a message: the card setters, {@code /bird}
 * and {@code /me} use {@link ArgumentParser#createStringFromFirstArgOnwards}, while {@code /whisper}, {@code /yell},
 * {@code /lo} and {@code /title} use {@link ArgumentParser#createStringFromArgs}.
 */
public class ArgumentParserTest {
    private final ArgumentParser argumentParser = new ArgumentParser();

    @Test
    public void firstArgOnwardsJoinsFromTheStartingIndexWithSingleSpaces() {
        String[] args = {"name", "Sir", "Roderick", "the", "Bold"};
        assertEquals("Sir Roderick the Bold", argumentParser.createStringFromFirstArgOnwards(args, 1));
    }

    @Test
    public void firstArgOnwardsFromZeroJoinsEveryArgument() {
        String[] args = {"draws", "a", "sword"};
        assertEquals("draws a sword", argumentParser.createStringFromFirstArgOnwards(args, 0));
    }

    @Test
    public void firstArgOnwardsWithASingleRemainingArgumentHasNoTrailingSpace() {
        String[] args = {"age", "42"};
        assertEquals("42", argumentParser.createStringFromFirstArgOnwards(args, 1));
    }

    @Test
    public void firstArgOnwardsPastTheLastArgumentIsEmpty() {
        // Callers check args.length first, but a start index at or past the end yields "", not an exception.
        assertEquals("", argumentParser.createStringFromFirstArgOnwards(new String[]{"name"}, 1));
        assertEquals("", argumentParser.createStringFromFirstArgOnwards(new String[0], 0));
    }

    @Test
    public void firstArgOnwardsKeepsEmptyArgumentsAsExtraSpaces() {
        // Bukkit passes consecutive spaces through as empty arguments; they are joined, not collapsed.
        String[] args = {"name", "Ser", "", "Brienne"};
        assertEquals("Ser  Brienne", argumentParser.createStringFromFirstArgOnwards(args, 1));
    }

    @Test
    public void argsJoinsEveryArgumentWithSingleSpaces() {
        String[] args = {"Is", "anyone", "there?"};
        assertEquals("Is anyone there?", argumentParser.createStringFromArgs(args));
    }

    @Test
    public void argsWithOneArgumentReturnsItUnchanged() {
        assertEquals("Hello", argumentParser.createStringFromArgs(new String[]{"Hello"}));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void argsWithNoArgumentsThrows() {
        // Every caller checks args.length > 0 before calling, so this is never reached from a command.
        argumentParser.createStringFromArgs(new String[0]);
    }
}
