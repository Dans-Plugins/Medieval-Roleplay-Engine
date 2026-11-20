package dansplugins.rpsystem.storage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AtomicFileWriterTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static String read(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
    }

    @Test
    public void writeCreatesTheFileWithTheGivenContent() throws IOException {
        File file = new File(temporaryFolder.getRoot(), "cards.txt");

        AtomicFileWriter.write(file, "a.txt\nb.txt\n");

        assertEquals("a.txt\nb.txt\n", read(file));
    }

    @Test
    public void writeReplacesTheWholeOfAnExistingFile() throws IOException {
        File file = temporaryFolder.newFile("cards.txt");
        Files.write(file.toPath(), "a much longer previous list of names\n".getBytes(Charset.defaultCharset()));

        AtomicFileWriter.write(file, "short\n");

        assertEquals("short\n", read(file));
    }

    @Test
    public void writeLeavesNoTemporaryFileBehind() throws IOException {
        File file = new File(temporaryFolder.getRoot(), "cards.txt");

        AtomicFileWriter.write(file, "a.txt\n");

        assertFalse(new File(temporaryFolder.getRoot(), "cards.txt.tmp").exists());
    }

    @Test
    public void writeOverwritesATemporaryFileLeftByAnInterruptedSave() throws IOException {
        // prepare - an earlier save was killed after writing part of the temp file
        File file = temporaryFolder.newFile("cards.txt");
        Files.write(file.toPath(), "kept.txt\n".getBytes(Charset.defaultCharset()));
        File staleTemp = temporaryFolder.newFile("cards.txt.tmp");
        Files.write(staleTemp.toPath(), "half-writ".getBytes(Charset.defaultCharset()));

        // execute
        AtomicFileWriter.write(file, "kept.txt\nnew.txt\n");

        // verify
        assertEquals("kept.txt\nnew.txt\n", read(file));
        assertFalse(staleTemp.exists());
    }

    @Test
    public void aFailedWriteLeavesThePreviousFileUntouched() throws IOException {
        // prepare - the temp path is a directory, so the write fails before anything is moved
        File file = temporaryFolder.newFile("cards.txt");
        byte[] previous = "a.txt\nb.txt\n".getBytes(Charset.defaultCharset());
        Files.write(file.toPath(), previous);
        assertTrue(new File(temporaryFolder.getRoot(), "cards.txt.tmp").mkdir());

        // execute
        try {
            AtomicFileWriter.write(file, "c.txt\n");
        } catch (IOException expected) {
            // the save reports its failure; what matters is the file on disk
        }

        // verify
        assertArrayEquals(previous, Files.readAllBytes(file.toPath()));
    }
}
