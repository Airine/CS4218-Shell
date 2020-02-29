package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class DiffApplicationTest {

    DiffInterface diffInterface;

    @BeforeAll
    static void startUp() {
        try {
            TestFileUtils.createSomeFiles();
            writeSomethingToFile(TestFileUtils.tempFileName1, "are you ok?");
            writeSomethingToFile(TestFileUtils.tempFileName2, "are you ook?");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeSomethingToFile(String fileName, String strs) throws IOException {
        try {
            OutputStream outputStream = IOUtils.openOutputStream(TestFileUtils.tempFileName1);
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
            assertThrows(Exception.class, () -> diffInterface.diffFileAndStdin(TestFileUtils.notExistFile, ioStream.in,
                    false, false, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    void testDiffFolder(){
//        assertDoesNotThrow(()->);
//    }


}
