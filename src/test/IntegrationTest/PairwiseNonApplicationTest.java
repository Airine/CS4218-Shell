package IntegrationTest;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PairwiseNonApplicationTest {
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
    void setup(){
        outputStream = new ByteArrayOutputStream();
        originPath = Environment.currentDirectory;
    }

    @AfterEach
    void reset(){
        assertDoesNotThrow(()->{
            outputStream.close();
        });
        Environment.currentDirectory = originPath;
        File file = new File(TEST_FILERESULT_PATH);
        try{
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Nested
    class positiveTest{
        @Test
        @DisplayName("ls src/test/IntegrationTest/testFiles/testFiles/test* | grep '1'")
        void testPipeAndGlobbing(){
            String commandString = "ls " + RELATIVE_PATH + CHAR_FILE_SEP + "test*" + " | grep '1'";
            String expectResult = "test1.txt" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("paste src/test/IntegrationTest/testFiles/test1.txt  | wc > src/test/IntegrationTest/testFiles/result.txt")
        void testPipeAndRedirection(){
            String commandString = "paste " + TEST_FILE1_PATH + " | wc -w > " + TEST_FILERESULT_PATH;
            String expectResult = "       2";
            File targetFile = new File(TEST_FILERESULT_PATH);
            assertDoesNotThrow(()->{
                BufferedReader reader = new BufferedReader(new FileReader(targetFile));
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, reader.readLine());
            });
        }

        @Test
        @DisplayName("paste src/test/IntegrationTest/testFiles/test1.txt | grep `echo 'wor'`")
        void testPipeAndQuoting(){
            String commandString = "paste " + TEST_FILE1_PATH + " | grep `echo 'wor'`";
            String expectResult = "world" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("cd src/test/IntegrationTest/testFiles; ls test*")
        void testGlobbingAndSemicolon(){
            String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; ls test*";
            String expectResult = "test1.txt test2.txt" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("wc src/test/IntegrationTest/testFiles/test* > src/test/IntegrationTest/testFiles/result.txt")
        void testGlobbingAndRedirection(){
            String commandString = "wc " + TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "test* > " + TEST_FILERESULT_PATH;
            String expectResult = "       1       2      12 src" + CHAR_FILE_SEP + "test" + CHAR_FILE_SEP +
                    "IntegrationTest" + CHAR_FILE_SEP + "testFiles" + CHAR_FILE_SEP + "test1.txt";
            File targetFile = new File(TEST_FILERESULT_PATH);
            assertDoesNotThrow(()->{
                BufferedReader reader = new BufferedReader(new FileReader(targetFile));
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, reader.readLine());
            });
        }

        @Test
        @DisplayName("echo `ls src/test/IntegrationTest/testFiles/test*`")
        void testGlobbingAndQuoting(){
            String commandString = "echo `ls " + TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "test*`";
            String expectResult = TEST_FILE1_PATH + " " + TEST_FILE2_PATH + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("echo `ls src/test/IntegrationTest/testFiles/test*` > src/test/IntegrationTest/testFiles/result.txt")
        void testRedirectionAndQuoting(){
            String commandString = "echo `ls " + TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "test*` > " + TEST_FILERESULT_PATH;
            String expectResult = TEST_FILE1_PATH + " " +TEST_FILE2_PATH;
            File targetFile = new File(TEST_FILERESULT_PATH);
            assertDoesNotThrow(()->{
                BufferedReader reader = new BufferedReader(new FileReader(targetFile));
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, reader.readLine());
            });
        }

        @Test
        @DisplayName("cd src/test/IntegrationTest/testFiles; paste test1.txt > result.txt")
        void testRedirectionAndSemicolon(){
            String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; paste test1.txt > result.txt";
            String expectResult = "hello";
            File targetFile = new File(TEST_FILERESULT_PATH);
            assertDoesNotThrow(()->{
                BufferedReader reader = new BufferedReader(new FileReader(targetFile));
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, reader.readLine());
            });
        }

        @Test
        @DisplayName("cd src/test/IntegrationTest/testFiles; echo `paste test1.txt`")
        void testQuotingAndSemicolon(){
            String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; echo `paste test1.txt`";
            String expectResult = "hello world" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }
    }

    @Nested
    class negativeTest{

    }
}
