package IntegrationTest;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PairwiseEF1Test {
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
    }

    @Nested
    class positiveTest {
        @Test
        @DisplayName("diff src/test/IntegrationTest/testFiles/test1.txt src/test/IntegrationTest/testFiles/test2.txt | grep 'w'")
        void testDiffAndGrep() {
            String commandString = "diff " + TEST_FILE1_PATH + " " + TEST_FILE2_PATH + "| grep 'hel'";
            String expectResult = "< hello" + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("diff src/test/IntegrationTest/testFiles/test1.txt src/test/IntegrationTest/testFiles/test2.txt | wc")
        void testDiffAndWc() {
            String commandString = "diff " + TEST_FILE1_PATH + " " + TEST_FILE2_PATH + "| wc -l";
            String expectResult = String.format(" %7d", 2) + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("cd src/test/IntegrationTest/testFiles; diff test1.txt test2.txt")
        void testDiffAndCd() {
            String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; diff test1.txt test2.txt";
            String expectResult = "< hello" + STRING_NEWLINE + "> goodbye" + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }


        @Test
        @DisplayName("grep 'world' src/test/IntegrationTest/testFiles/test1.txt | wc -c")
        void testGrepAndWc() {
            String commandString = "grep 'world' " + TEST_FILE1_PATH + "| wc -c";
            String expectResult = String.format(" %7d", "world".length() + STRING_NEWLINE.length()) + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("cd src/test/IntegrationTest/testFiles; grep 'wor' test1.txt")
        void testGrepAndCd() {
            String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; grep 'wor' test1.txt";
            String expectResult = "world" + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }


        @Test
        @DisplayName("cd src/test/IntegrationTest/testFiles; wc -c test1.txt")
        void testWcAndCd() {
            String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; wc -c test1.txt";
            String expectResult = String.format(" %7d test1.txt", 10+STRING_NEWLINE.length()) + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }


        @Test
        @DisplayName("cd src/test/IntegrationTest/testFiles; cp test1.txt result.txt")
        void testCdAndCp() {
            String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; cp test1.txt result.txt";
            String expectResult = "hello";
            File targetFile = new File(TEST_FILERESULT_PATH);
            assertDoesNotThrow(() -> {
                BufferedReader reader = new BufferedReader(new FileReader(targetFile));
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, reader.readLine());
            });
        }
    }

    @Nested
    class negativeTest {
        @Test
        @DisplayName("cd src/test/IntegrationTest; wc test1.txt")
        void testCdAndWc() {
            String commandString = "cd src/test/IntegrationTest; wc test1.txt";
            String expectResult = "wc: No such file or directory" + STRING_NEWLINE;
            assertDoesNotThrow(() -> {
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }
    }


}