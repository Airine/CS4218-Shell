package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class LsApplicationTest {

    private static final String SUB_DIR = "subDir";
    private static final String TEST_TXT = "test.txt";
    private static final String SUB_SUB_DIR = "subSubDir";
    private final LsInterface lsApplication = new LsApplication();
    private OutputStream outputStream;
    private final String cwd = Environment.currentDirectory;

    @BeforeEach
    void setCurrentDirectory() {
        //noinspection NonAtomicOperationOnVolatileField
        Environment.currentDirectory += CHAR_FILE_SEP + "asset" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "ls";
        outputStream = new ByteArrayOutputStream();
    }


    @AfterEach
    void resetCurrentDirectory() {
        Environment.currentDirectory = cwd;
    }

    @Test
    void runWithNullArgs() {
        assertThrows(LsException.class, () -> {
            lsApplication.run(null, System.in, outputStream);
        });
    }

    @Test
    void runWithNullOutputStream() {
        String[] args = {};
        assertThrows(LsException.class, () -> {
            lsApplication.run(args, System.in, null);
        });
    }

    @Test
    void runWithClosedOutputStream() {
        String[] args = {};
        assertThrows(LsException.class, () -> {
            outputStream = new FileOutputStream(new File(TEST_TXT));
            outputStream.close();
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void runWithInvalidArgs() {
        String[] args = {"-a"};
        assertThrows(LsException.class, () -> {
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void runWithValidArgs() {
        String[] args = {"-R", "-d"};
        assertDoesNotThrow(() -> {
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void runWithMultiArgs() {
        String[] args = {TEST_TXT, SUB_DIR};
        String result = TEST_TXT + STRING_NEWLINE +
                STRING_NEWLINE +
                SUB_DIR + ":" + STRING_NEWLINE +
                SUB_SUB_DIR + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            lsApplication.run(args, System.in, outputStream);
            assertEquals(result, outputStream.toString());

        });
    }


    @Test
    void runWithNonExistFile() {
        String[] args = {TEST_TXT, SUB_DIR, "none.txt"};
        String result = TEST_TXT + STRING_NEWLINE +
                STRING_NEWLINE +
                SUB_DIR + ":" + STRING_NEWLINE +
                SUB_SUB_DIR + STRING_NEWLINE +
                STRING_NEWLINE +
                "ls: cannot access 'none.txt': No such file or directory" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            lsApplication.run(args, System.in, outputStream);
            assertEquals(result, outputStream.toString());
        });
    }

    @Test
    void runWithWrongOutputStream() {
        String[] args = {};
        try {
            try (OutputStream outputStreamTest = IOUtils.openOutputStream("asset/test.txt")) {
                IOUtils.closeOutputStream(outputStreamTest);
                assertThrows(LsException.class, () -> {
                    lsApplication.run(args, System.in, outputStreamTest);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ShellException e) {
            e.printStackTrace();
        }
    }

    @Test
    void listFolderContentWithEmptyFolderName() {
        String result = SUB_DIR + STRING_NEWLINE +
                "subDir1" + STRING_NEWLINE +
                "subDir2" + STRING_NEWLINE +
                TEST_TXT + STRING_NEWLINE +
                "test1.txt" + STRING_NEWLINE +
                "test2.txt";
        assertDoesNotThrow(() -> {
            assertEquals(result, lsApplication.listFolderContent(false, false));
        });
        Environment.currentDirectory = cwd;
    }

    @Test
    void listFolderContentWithOnlyFolderFlag() {
        String result = SUB_DIR + ":" + STRING_NEWLINE + SUB_SUB_DIR;
        assertDoesNotThrow(() -> {
            assertEquals(result, lsApplication.listFolderContent(true, false, SUB_DIR));
        });
    }

    @Test
    void listFolderContentWithRecursionFlag() {
        String result = SUB_DIR + ":" + STRING_NEWLINE +
                SUB_SUB_DIR + STRING_NEWLINE +
                STRING_NEWLINE +
                SUB_DIR + CHAR_FILE_SEP + "subSubDir:" + STRING_NEWLINE +
                TEST_TXT;
        assertDoesNotThrow(() -> {
            assertEquals(result, lsApplication.listFolderContent(false, true, SUB_DIR));
        });
    }

    @Test
    void listCwdContentWithWrongCwd() {
        //noinspection NonAtomicOperationOnVolatileField
        Environment.currentDirectory += CHAR_FILE_SEP + "none" + CHAR_FILE_SEP;
        assertThrows(LsException.class, () -> {
            lsApplication.listFolderContent(false, false);
        });
        Environment.currentDirectory = cwd;
    }

}