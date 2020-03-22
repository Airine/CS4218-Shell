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
        @DisplayName("cut -c 1 `ls src/test/IntegrationTest/testFiles/test*`")
        void testCutAndLs(){
            String commandString = "cut -c 1 `ls " + TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "test*`";
            String expectResult = "h" + STRING_NEWLINE + "w" + STRING_NEWLINE + "g" + STRING_NEWLINE + "w" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("cut -c 5 src/test/IntegrationTest/testFiles/test1.txt | sort")
        void testCutAndSort(){
            String commandString = "cut -c 5 " + TEST_FILE1_PATH + " | sort";
            String expectResult = "d" + STRING_NEWLINE + "o" + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("find src/test/IntegrationTest/testFiles -name 'test*.txt' | cut -c 1")
        void testCutAndFind(){
            String commandString = "find " + TEST_FILE_FOLDER_PATH + "-name 'test*.txt' | cut -c 1";
            String expectResult = "test";
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("ls src/test/IntegrationTest/testFiles/test* | sort")
        void testLsAndSort(){
            String commandString = "ls " + TEST_FILE_FOLDER_PATH + CHAR_FILE_SEP + "test* | sort";
            String expectResult = TEST_FILE1_PATH + STRING_NEWLINE + TEST_FILE2_PATH + STRING_NEWLINE;
            assertDoesNotThrow(()->{
                shell.parseAndEvaluate(commandString, outputStream);
                assertEquals(expectResult, outputStream.toString());
            });
        }

        @Test
        @DisplayName("cd src/test/IntegrationTest; find ./ -name 'test' | sort")
        void testSortAndFind(){
            String commandString = "cd src/test/IntegrationTest; find ./ -name 'test' | sort";
            String expectResult = "testFiles/test1.txt" + STRING_NEWLINE + "testFiles/test2.txt" + STRING_NEWLINE;
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