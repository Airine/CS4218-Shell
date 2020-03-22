package IntegrationTest;

import org.junit.jupiter.api.*;
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
    private static final String TEST_FILE3_PATH = TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "test3.txt";


    Shell shell = new ShellImpl();
    ByteArrayOutputStream outputStream;

    @BeforeEach
    void setup(){
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset(){
        assertDoesNotThrow(()->{
            outputStream.close();
        });
    }

    @Test
    @DisplayName("diff src/test/IntegrationTest/testFiles/test1.txt src/test/IntegrationTest/testFiles/test2.txt | grep 'w'")
    void testDiffAndGrep(){
        String commandString = "diff " + TEST_FILE1_PATH + TEST_FILE2_PATH + "| grep 'hel'";
        String expectResult = "< hello";
        assertDoesNotThrow(()->{
            shell.parseAndEvaluate(commandString, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    @DisplayName("diff src/test/IntegrationTest/testFiles/test1.txt src/test/IntegrationTest/testFiles/test2.txt | wc")
    void testDiffAndWc(){
        String commandString = "diff " + TEST_FILE1_PATH + TEST_FILE2_PATH + "| wc -l";
        String expectResult = "2";
        assertDoesNotThrow(()->{
            shell.parseAndEvaluate(commandString, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }

    @Test
    @DisplayName("cd src/test/IntegrationTest/testFiles; diff test1.txt test2.txt")
    void testDiffAndCd(){
        String commandString = "cd " + TEST_FILE_FOLDER_PATH + "; diff test1.txt test2.txt";
        String expectResult = "< hello" + STRING_NEWLINE + "> goodbye";
        assertDoesNotThrow(()->{
            shell.parseAndEvaluate(commandString, outputStream);
            assertEquals(expectResult, outputStream.toString());
        });
    }


}