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

public class PairwiseEF2Test {
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
    }

    @Nested
    class positiveTest{
        @Test
        @DisplayName("need help")
        void testWcAndCp(){
            String commandString = "";
            String expectResult = "";
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