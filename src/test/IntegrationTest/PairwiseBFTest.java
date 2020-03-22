package IntegrationTest;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PairwiseBFTest {
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
        File file = new File(TEST_FILERESULT_PATH);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        @DisplayName("echo 'hello world' | paste -")
        void testEchoAndPaste(){
            String commandString = "echo 'hello world' | paste -";
            String expectResult = "hello world";
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("echo `sed 's/hello/goodbye/' src/test/IntegrationTest/testFiles/test1.txt`")
        void testEchoAndSed(){
            String commandString = "echo `sed 's/hello/goodbye/' " + TEST_FILE1_PATH +"`";
            String expectResult = "goodbye world" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("paste src/test/IntegrationTest/testFiles/test1.txt | sed 's/hello/goodbye/'")
        void testPasteAndSed(){
            String commandString = "paste " + TEST_FILE1_PATH + " | sed 's/hello/goodbye/'";
            String expectResult = "goodbye" + STRING_NEWLINE + "world" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }
    }

    @Nested
    class negativeTest{
        @Test
        @DisplayName("rm src/test/IntegrationTest/testFiles/result.txt; paste src/test/IntegrationTest/testFiles/result.txt")
        void testRmAndPaste(){
            String commandString = "rm " + TEST_FILERESULT_PATH + "; paste " + TEST_FILERESULT_PATH;
            String expectResult = "paste: src/test/IntegrationTest/testFiles/result.txt: No such file or directory" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("sed `echo 's///'` src/test/IntegrationTest/testFiles/test1.txt")
        void testEchoAndSed(){
            String commandString = "sed `echo 's///'` " + TEST_FILE1_PATH;
            String expectResult = "paste: src/test/IntegrationTest/testFiles/result.txt: No such file or directory" + STRING_NEWLINE;
            assertThrows(SedException.class,
                    () -> shell.parseAndEvaluate(commandString, outputStream));
        }
    }

}