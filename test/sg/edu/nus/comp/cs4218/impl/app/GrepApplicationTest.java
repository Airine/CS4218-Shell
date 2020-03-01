package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.app.EchoInterface;
import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class GrepApplicationTest {
    private static final String HELLOWORLD = "HelloWorld";
    private static final String DHELLOWORL = "dHelloWorl";
    private static final String LDHELLOWOR = "ldHelloWor";
    private static final String RLDHELLOWO = "rldHelloWo";
    private static final String ORLDHELLOW = "orldHelloW";
    private static final String WORLDHELLO = "WorldHello";
    private static final String OWORLDHELL = "oWorldHell";
    private static final String LOWORLDHEL = "loWorldHel";
    private static final String LLOWORLDHE = "lloWorldHe";
    private static final String ELLOWORLDH = "elloWorldH";
    private static final String ERR_PREFIX = "grep: ";

    private final GrepInterface grepApp = new GrepApplication();
    private static final String TEST_STRING_1 = HELLOWORLD + STRING_NEWLINE +
                                DHELLOWORL + STRING_NEWLINE +
                                LDHELLOWOR + STRING_NEWLINE +
                                RLDHELLOWO + STRING_NEWLINE +
                                ORLDHELLOW + STRING_NEWLINE +
                                WORLDHELLO + STRING_NEWLINE +
                                OWORLDHELL + STRING_NEWLINE +
                                LOWORLDHEL + STRING_NEWLINE +
                                LLOWORLDHE + STRING_NEWLINE +
                                ELLOWORLDH;

    private static final String TEST_STRING_2 = "HarryPotter" + STRING_NEWLINE +
                                "rHarryPotte" + STRING_NEWLINE +
                                "erHarryPott" + STRING_NEWLINE +
                                "terHarryPot" + STRING_NEWLINE +
                                "tterHarryPo" + STRING_NEWLINE +
                                "otterHarryP" + STRING_NEWLINE +
                                "PotterHarry" + STRING_NEWLINE +
                                "yPotterHarr" + STRING_NEWLINE +
                                "ryPotterHar" + STRING_NEWLINE +
                                "rryPotterHa" + STRING_NEWLINE +
                                "arryPotterH";

    private static final String TEST_PATTERN_1 = "world";
    private static final String TEST_PATTERN_2 = "World";


    @BeforeAll
    static void setup(){
        try {
            TestFileUtils.createSomeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (FileOutputStream outputStream1 = new FileOutputStream(TestFileUtils.tempFileName1)){
            outputStream1.write(TEST_STRING_1.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream outputStream2 = new FileOutputStream(TestFileUtils.tempFileName2)){
            outputStream2.write(TEST_STRING_2.getBytes());
            outputStream2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown(){
        TestFileUtils.rmCreatedFiles();
    }

    @Test
    void testGrepFromFileNullFile(){
        String pattern = "pattern";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        String[] targetFiles = null;
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.grepFromFiles(pattern, isCaseInsensitive, isCountLines, targetFiles);
        });
        assertEquals(ERR_PREFIX + NULL_POINTER, thrown.getMessage());
    }

    @Test
    void testGrepFromFileNullPattern(){
        String pattern = null;
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        String[] targetFiles = {TestFileUtils.tempFileName1};
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.grepFromFiles(pattern, isCaseInsensitive, isCountLines, targetFiles);
        });
        assertEquals(ERR_PREFIX + NULL_POINTER, thrown.getMessage());
    }

    @Test
    void testGrepFromFileIsCaseInsensitive(){
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = false;
        String[] targetFiles = {TestFileUtils.tempFileName1};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(TEST_PATTERN_1, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals(HELLOWORLD + STRING_NEWLINE +
                                    WORLDHELLO + STRING_NEWLINE +
                                    OWORLDHELL + STRING_NEWLINE +
                                    LOWORLDHEL + STRING_NEWLINE +
                                    LLOWORLDHE + STRING_NEWLINE +
                                    ELLOWORLDH + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileIsCountLines(){
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        String[] targetFiles = {TestFileUtils.tempFileName1};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(TEST_PATTERN_1, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals("0" + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileInvalidFile(){
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        String[] targetFiles = {TestFileUtils.tempFileName1 + "1"};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(TEST_PATTERN_1, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals(TestFileUtils.tempFileName1 + "1: " + ERR_FILE_NOT_FOUND + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileIsDirectory(){
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        String[] targetFiles = {TestFileUtils.tempFolderName};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(TEST_PATTERN_1, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals(TestFileUtils.tempFolderName + ": " + IS_DIRECTORY + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileMultiFile(){
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        String[] targetFiles = {TestFileUtils.tempFileName1, TestFileUtils.tempFileName2};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(TEST_PATTERN_2, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals(TestFileUtils.tempFileName1 + ": 6" + STRING_NEWLINE + TestFileUtils.tempFileName2 + ": 0" + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileInvalidPattern(){
        String pattern = "&^#@%@(*^&@^(*&#%^^%@";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        String[] targetFiles = {TestFileUtils.tempFileName1};
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.grepFromFiles(pattern, isCaseInsensitive, isCountLines, targetFiles);
        });
        assertEquals(ERR_PREFIX + ERR_INVALID_REGEX, thrown.getMessage());
    }

    @Test
    void testGrepFromStdinIsCaseInsensitive(){
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = false;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(TEST_STRING_1.getBytes());
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromStdin(TEST_PATTERN_1, isCaseInsensitive, isCountLines, inputStream);
            assertEquals(HELLOWORLD + STRING_NEWLINE +
                    WORLDHELLO + STRING_NEWLINE +
                    OWORLDHELL + STRING_NEWLINE +
                    LOWORLDHEL + STRING_NEWLINE +
                    LLOWORLDHE + STRING_NEWLINE +
                    ELLOWORLDH + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromStdinIsCountLines(){
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(TEST_STRING_1.getBytes());
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromStdin(TEST_PATTERN_1, isCaseInsensitive, isCountLines, inputStream);
            assertEquals("0" + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromStdinInvalidPattern(){
        String pattern = "&^#@%@(*^&@^(*&#%^^%@";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(TEST_STRING_1.getBytes());
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        });
        assertEquals(ERR_PREFIX + ERR_INVALID_REGEX, thrown.getMessage());
    }

    @Test
    void testGrepFromStdinNullInput(){
        String pattern = "&^#@%@(*^&@^(*&#%^^%@";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.grepFromStdin(pattern, isCaseInsensitive, isCountLines, null);
        });
        assertEquals(ERR_PREFIX + ERR_FILE_NOT_FOUND, thrown.getMessage());
    }


    @Test
    void testRunNoInput(){
        String[] args = {"-i", "-c", TEST_PATTERN_2};
        Throwable thrown = assertThrows(Exception.class, ()->{
            grepApp.run(args, null, System.out);
        });
        assertEquals(ERR_PREFIX + ERR_NO_INPUT, thrown.getMessage());
    }

    @Test
    void testRunEmptyPattern(){
        String[] args = {"-i", "-c", "", TestFileUtils.tempFileName1};
        Throwable thrown = assertThrows(Exception.class, ()->{
            grepApp.run(args, null, System.out);
        });
        assertEquals(ERR_PREFIX + EMPTY_PATTERN, thrown.getMessage());
    }

    @Test
    void testRunNoPattern(){
        String[] args = {};
        Throwable thrown = assertThrows(Exception.class, ()->{
            grepApp.run(args, System.in, System.out);
        });
        assertEquals(ERR_PREFIX + ERR_SYNTAX, thrown.getMessage());
    }

    @Test
    void testRunFile(){
        String[] args = {"-i", TEST_PATTERN_2, TestFileUtils.tempFileName1};
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        assertDoesNotThrow(()->{
            grepApp.run(args, System.in, stdout);
            assertEquals(HELLOWORLD + STRING_NEWLINE +
                    WORLDHELLO + STRING_NEWLINE +
                    OWORLDHELL + STRING_NEWLINE +
                    LOWORLDHEL + STRING_NEWLINE +
                    LLOWORLDHE + STRING_NEWLINE +
                    ELLOWORLDH + STRING_NEWLINE, stdout.toString());
        });
    }

    @Test
    void testRunStdin(){
        String[] args = {"-i", "-c", TEST_PATTERN_2};
        ByteArrayInputStream stdin = new ByteArrayInputStream(TEST_STRING_1.getBytes());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        assertDoesNotThrow(()->{
            grepApp.run(args, stdin, stdout);
            assertEquals("6" + STRING_NEWLINE, stdout.toString());
        });
    }

    @Test
    void testRunInvalidSyntax(){
        String[] args = {"-l", TEST_PATTERN_2, TestFileUtils.tempFileName1};
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.run(args, System.in, stdout);
        });
        assertEquals(ERR_PREFIX + ERR_SYNTAX, thrown.getMessage());
    }
}