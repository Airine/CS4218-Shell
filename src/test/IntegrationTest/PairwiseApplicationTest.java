package IntegrationTest;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PairwiseApplicationTest {
    private static final String RELATIVE_PATH = "src" + CHAR_FILE_SEP + "test" + CHAR_FILE_SEP + "IntegrationTest";
    //    private static final String CURRENT_PATH = System.getProperty("user.dir") + CHAR_FILE_SEP + RELATIVE_PATH;
    private static final String TEST_FILE_FOLDER_PATH = RELATIVE_PATH + CHAR_FILE_SEP + "testFiles";
    private static final String TEST_FILE1_PATH = TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "test1.txt";
    private static final String TEST_FILE2_PATH = TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "test2.txt";
    private static final String TEST_FILERESULT_PATH = TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "result.txt";

    private String originPath;

    Shell shell = new ShellImpl();
    ByteArrayOutputStream outputStream;

    @BeforeEach
    void setup() {
        outputStream = new ByteArrayOutputStream();
        originPath = Environment.currentDirectory;
    }

    @AfterEach
    void reset() {
        assertDoesNotThrow(() -> {
            outputStream.close();
        });
        Environment.currentDirectory = originPath;
        File file = new File(TEST_FILERESULT_PATH);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file1 = new File(TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "result1.txt");
        if (file1.exists()) {
            file1.delete();
        }
    }

    @Nested
    class positiveTest {
        @Test
        @DisplayName("echo `diff src/test/IntegrationTest/testFiles/test1.txt src/test/IntegrationTest/testFiles/test2.txt`")
        void testEchoAndDirr() {
            String commandString = "echo `diff " + TEST_FILE1_PATH + " " + TEST_FILE2_PATH + "`";
            String expectResult = "< hello > goodbye" + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("paste src/test/IntegrationTest/testFiles/test1.txt | grep 'wor'")
        void testPasteAndGrep() {
            String commandString = "paste " + TEST_FILE1_PATH + " | grep 'wor'";
            String expectResult = "world" + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("sed 's/hello//' src/test/IntegrationTest/testFiles/test1.txt | wc -c")
        void testSedAndWc() {
            String commandString = "sed 's/hello//' " + TEST_FILE1_PATH + " | wc -c";
            String expectResult = String.format(" %7d", 7) + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("cd src/test/IntegrationTest/testFiles; cut -c 1 test1.txt")
        void testCdAndCut() {
            String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; cut -c 1 test1.txt";
            String expectResult = "h" + STRING_NEWLINE + "w" + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("cp `find src/test/IntegrationTest/testFiles -name 'test1.txt'` src/test/IntegrationTest/testFiles/result.txt")
        void testCpAndFind() {
            String commandString = "cp `find " + TEST_FILE_FOLDER_PATH + " -name 'test1.txt'` " + TEST_FILERESULT_PATH;
            String expectResult = "hello";
            File targetFile = new File(TEST_FILERESULT_PATH);
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                BufferedReader reader = new BufferedReader(new FileReader(targetFile));
                assertEquals(expectResult, reader.readLine());
            });
        }


    }

    @Nested
    class negativeTest {

    }
}