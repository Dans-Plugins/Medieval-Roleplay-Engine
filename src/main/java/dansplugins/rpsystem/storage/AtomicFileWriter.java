package dansplugins.rpsystem.storage;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Replaces a file's contents in one step. The text is written to a sibling {@code .tmp} file
 * that is then moved over the target, so a server killed or a disk filled part way through a
 * save leaves the previous file whole instead of a truncated one. A truncated {@code cards.txt}
 * would drop every card listed after the cut from the next load, and the save after that would
 * write the shortened list back, leaving those cards' files on disk but never read again; a
 * truncated card file would load with the missing lines at their defaults.
 *
 * <p>The text is written in the platform's default charset, the one {@link java.io.FileWriter}
 * used before and {@link java.util.Scanner} still reads with, so existing files read back
 * unchanged.</p>
 */
public final class AtomicFileWriter {
    private AtomicFileWriter() {
    }

    public static void write(File file, String content) throws IOException {
        Path target = file.toPath();
        Path temp = target.resolveSibling(file.getName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, Charset.defaultCharset())) {
            writer.write(content);
        }
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
