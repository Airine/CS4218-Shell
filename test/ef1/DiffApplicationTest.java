package ef1;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.NewIOStream;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class DiffApplicationTest {

    private static String testText1 = "are you ok?";
    private static String testText2 = "are you ook?";
    DiffInterface diffInterface;

    @BeforeAll
    static void startUp() {
        try {
            TestFileUtils.createSomeFiles();
            writeSomethingToFile(TestFileUtils.tempFileName1, testText1);
            writeSomethingToFile(TestFileUtils.tempFileName2, testText2);

            writeSomethingToFile(TestFileUtils.tempFileInFolder, testText1);
            writeSomethingToFile(TestFileUtils.tempFileInFolder2, testText1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeSomethingToFile(String fileName, String strs) throws IOException {
        try {
//            specific method to close this output stream
            OutputStream outputStream = IOUtils.openOutputStream(TestFileUtils.tempFileName1);//NOPMD
            outputStream.write(strs.getBytes());
            IOUtils.closeOutputStream(outputStream);
        } catch (ShellException e) {
            throw new IOException(e.getMessage(), e);
        }
    }


    @AfterAll
    static void tearDown() {
        TestFileUtils.rmCreatedFiles();
    }


    @Test
    void testIdentityFile() {
        final String[] diff = new String[1];
        assertDoesNotThrow(() -> {
            diff[0] = diffInterface.diffTwoFiles(TestFileUtils.tempFileName1, TestFileUtils.tempFileName1,
                    false, false, false);
        });
        assertEquals("", diff[0]);
    }

    @Test
    void testDifferentFile() {
        final String[] diff = new String[1];
        assertDoesNotThrow(() -> {
            diff[0] = diffInterface.diffTwoFiles(TestFileUtils.tempFileName1, TestFileUtils.tempFileName2,
                    false, false, false);
        });
        assertFalse(StringUtils.isBlank(diff[0]));
    }

    @Test
    void testDiffNotExistFile() {
        assertThrows(Exception.class, () -> diffInterface.diffTwoFiles(TestFileUtils.tempFileName1, TestFileUtils.notExistFile,
                false, false, false));
        assertThrows(Exception.class, () -> diffInterface.diffTwoFiles(TestFileUtils.notExistFile, TestFileUtils.tempFileName1,
                false, false, false));
        assertThrows(Exception.class, () -> diffInterface.diffTwoDir(TestFileUtils.tempFolderName, TestFileUtils.notExistFile,
                false, false, false));
        try (NewIOStream ioStream = new NewIOStream(TestFileUtils.tempFileName2, TestFileUtils.tempFileName2)) {
            assertThrows(Exception.class, () -> diffInterface.diffFileAndStdin(TestFileUtils.notExistFile, ioStream.inputStream,
                    false, false, false));
        } catch (IOException e) {
            e.printStackTrace();
            fail("should not throw exception");
        }
    }

    @Test
    void testDiffFolder() {
        final String[] diff = new String[1];
        assertDoesNotThrow(() -> diff[0] = diffInterface.diffTwoDir(TestFileUtils.emptyFolderName, TestFileUtils.tempFolderName,
                false, false, false));
        assertFalse(StringUtils.isBlank(diff[0]));
    }

    @Test
    void testIdentityFolder() {
        final String[] diff = new String[1];
        assertDoesNotThrow(() -> diff[0] = diffInterface.diffTwoDir(TestFileUtils.tempFolderName, TestFileUtils.tempFolderName2,
                false, false, false));
        assertTrue(StringUtils.isBlank(diff[0]));
    }

    @Test
    void testIdentiyFileAndInputStream() {
        final String[] diff = new String[1];
        try (NewIOStream ioStream = new NewIOStream(TestFileUtils.tempFileName2, TestFileUtils.tempFileName2)) {
            InputStream inputStream = new ByteArrayInputStream(testText1.getBytes());
            assertDoesNotThrow(() -> diff[0] = diffInterface.diffFileAndStdin(TestFileUtils.tempFileName1, inputStream,
                    false, false, false));
            assertTrue(StringUtils.isBlank(diff[0]));
        } catch (IOException e) {
            e.printStackTrace();
            fail("should not throw exception");
        }
    }

    @Test
    void testDiffFileAndInputStream() {
        final String[] diff = new String[1];
        InputStream inputStream = new ByteArrayInputStream(testText2.getBytes());
        assertDoesNotThrow(() -> diff[0] = diffInterface.diffFileAndStdin(TestFileUtils.tempFileName1, inputStream,
                false, false, false));
        assertFalse(StringUtils.isBlank(diff[0]));
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRunWithFile() {
        String[] args = {TestFileUtils.tempFileName1, TestFileUtils.tempFileName1};
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> diffInterface.run(args, System.in, outputStream));
        assertTrue(StringUtils.isBlank(outputStream.toString()));
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testRunWithFileAndFlag() {
        String[] args = {"-s", TestFileUtils.tempFileName1, TestFileUtils.tempFileName1};
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> diffInterface.run(args, System.in, outputStream));
        assertFalse(StringUtils.isBlank(outputStream.toString()));
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testRunWithStdin() {
        String[] args = {TestFileUtils.tempFileName1, "-"};
        InputStream inputStream = new ByteArrayInputStream((testText1 + "\n     ").getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> diffInterface.run(args, inputStream, outputStream));
        assertFalse(StringUtils.isBlank(outputStream.toString()));
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRunWithStdinAndFlag() {
        String[] args = {"-B", TestFileUtils.tempFileName1, "-"};
        InputStream inputStream = new ByteArrayInputStream((testText1 + "\n     ").getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> diffInterface.run(args, inputStream, outputStream));
        assertTrue(StringUtils.isBlank(outputStream.toString()));
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
