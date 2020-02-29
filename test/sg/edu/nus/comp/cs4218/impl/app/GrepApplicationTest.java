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
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.EMPTY_PATTERN;
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.IS_DIRECTORY;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class GrepApplicationTest {
    private final GrepInterface grepApp = new GrepApplication();

    @BeforeAll
    static void setup(){

        try {
            TestFileUtils.createSomeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream outputStream1 = new FileOutputStream(TestFileUtils.tempFileName1);
            try {
                outputStream1.write(("HelloWorld\n" +
                                    "dHelloWorl\n" +
                                    "ldHelloWor\n" +
                                    "rldHelloWo\n" +
                                    "orldHelloW\n" +
                                    "WorldHello\n" +
                                    "oWorldHell\n" +
                                    "loWorldHel\n" +
                                    "lloWorldHe\n" +
                                    "elloWorldH").getBytes());
                try {
                    IOUtils.closeOutputStream(outputStream1);
                } catch (ShellException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream outputStream2 = new FileOutputStream(TestFileUtils.tempFileName2);
            try {
                outputStream2.write(("HarryPotter" +
                                    "\nrHarryPotte" +
                                    "\nerHarryPott" +
                                    "\nterHarryPot" +
                                    "\ntterHarryPo" +
                                    "\notterHarryP" +
                                    "\nPotterHarry" +
                                    "\nyPotterHarr" +
                                    "\nryPotterHar" +
                                    "\nrryPotterHa" +
                                    "\narryPotterH").getBytes());
                try {
                    IOUtils.closeOutputStream(outputStream2);
                } catch (ShellException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
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
        assertEquals("grep: Null Pointer Exception", thrown.getMessage());
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
        assertEquals("grep: Null Pointer Exception", thrown.getMessage());
    }

    @Test
    void testGrepFromFileIsCaseInsensitive(){
        String pattern = "world";
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = false;
        String[] targetFiles = {TestFileUtils.tempFileName1};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(pattern, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals("HelloWorld" + STRING_NEWLINE +
                                    "WorldHello" + STRING_NEWLINE +
                                    "oWorldHell" + STRING_NEWLINE +
                                    "loWorldHel" + STRING_NEWLINE +
                                    "lloWorldHe" + STRING_NEWLINE +
                                    "elloWorldH" + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileIsCountLines(){
        String pattern = "world";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        String[] targetFiles = {TestFileUtils.tempFileName1};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(pattern, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals("0" + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileInvalidFile(){
        String pattern = "world";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = false;
        String[] targetFiles = {TestFileUtils.tempFileName1 + "1"};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(pattern, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals(TestFileUtils.tempFileName1 + "1: " + ERR_FILE_NOT_FOUND + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileIsDirectory(){
        String pattern = "world";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        String[] targetFiles = {TestFileUtils.tempFolderName};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(pattern, isCaseInsensitive, isCountLines, targetFiles);
            assertEquals(TestFileUtils.tempFolderName + ": " + IS_DIRECTORY + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromFileMultiFile(){
        String pattern = "World";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        String[] targetFiles = {TestFileUtils.tempFileName1, TestFileUtils.tempFileName2};
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromFiles(pattern, isCaseInsensitive, isCountLines, targetFiles);
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
        assertEquals("grep: " + ERR_INVALID_REGEX, thrown.getMessage());
    }

    @Test
    void testGrepFromStdinIsCaseInsensitive() throws FileNotFoundException {
        String pattern = "world";
        Boolean isCaseInsensitive = true;
        Boolean isCountLines = false;
        FileInputStream inputStream = new FileInputStream(TestFileUtils.tempFileName1);
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
            assertEquals("HelloWorld" + STRING_NEWLINE +
                    "WorldHello" + STRING_NEWLINE +
                    "oWorldHell" + STRING_NEWLINE +
                    "loWorldHel" + STRING_NEWLINE +
                    "lloWorldHe" + STRING_NEWLINE +
                    "elloWorldH" + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromStdinIsCountLines() throws FileNotFoundException {
        String pattern = "world";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        FileInputStream inputStream = new FileInputStream(TestFileUtils.tempFileName1);
        assertDoesNotThrow(()->{
            String result = grepApp.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
            assertEquals("0" + STRING_NEWLINE, result);
        });
    }

    @Test
    void testGrepFromStdinInvalidPattern() throws FileNotFoundException {
        String pattern = "&^#@%@(*^&@^(*&#%^^%@";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        FileInputStream inputStream = new FileInputStream(TestFileUtils.tempFileName1);
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        });
        assertEquals("grep: " + ERR_INVALID_REGEX, thrown.getMessage());
    }

    @Test
    void testGrepFromStdinNullInput() throws FileNotFoundException {
        String pattern = "&^#@%@(*^&@^(*&#%^^%@";
        Boolean isCaseInsensitive = false;
        Boolean isCountLines = true;
        InputStream inputStream = null;
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.grepFromStdin(pattern, isCaseInsensitive, isCountLines, inputStream);
        });
        assertEquals("grep: " + ERR_FILE_NOT_FOUND, thrown.getMessage());
    }


    @Test
    void testRunNoInput(){
        String[] args = {"-i", "-c", "World"};
        InputStream stdin = null;
        OutputStream stdout = System.out;
        Throwable thrown = assertThrows(Exception.class, ()->{
            grepApp.run(args, stdin, stdout);
        });
        assertEquals("grep: No InputStream and no filenames", thrown.getMessage());
    }

    @Test
    void testRunEmptyPattern(){
        String[] args = {"-i", "-c", "", TestFileUtils.tempFileName1};
        InputStream stdin = null;
        OutputStream stdout = System.out;
        Throwable thrown = assertThrows(Exception.class, ()->{
            grepApp.run(args, stdin, stdout);
        });
        assertEquals("grep: " + EMPTY_PATTERN, thrown.getMessage());
    }

    @Test
    void testRunNoPattern(){
        String[] args = {};
        InputStream stdin = System.in;
        OutputStream stdout = System.out;
        Throwable thrown = assertThrows(Exception.class, ()->{
            grepApp.run(args, stdin, stdout);
        });
        assertEquals("grep: " + ERR_SYNTAX, thrown.getMessage());
    }

    @Test
    void testRunFile(){
        String[] args = {"-i", "World", TestFileUtils.tempFileName1};
        InputStream stdin = System.in;
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        assertDoesNotThrow(()->{
            grepApp.run(args, stdin, stdout);
            assertEquals("HelloWorld" + STRING_NEWLINE +
                    "WorldHello" + STRING_NEWLINE +
                    "oWorldHell" + STRING_NEWLINE +
                    "loWorldHel" + STRING_NEWLINE +
                    "lloWorldHe" + STRING_NEWLINE +
                    "elloWorldH" + STRING_NEWLINE, stdout.toString());
        });
    }

    @Test
    void testRunStdin() throws FileNotFoundException {
        String[] args = {"-i", "-c", "World"};
        FileInputStream stdin = new FileInputStream(TestFileUtils.tempFileName1);
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        assertDoesNotThrow(()->{
            grepApp.run(args, stdin, stdout);
            assertEquals("6" + STRING_NEWLINE, stdout.toString());
        });
    }

    @Test
    void testRunInvalidSyntax(){
        String[] args = {"-l", "World", TestFileUtils.tempFileName1};
        InputStream stdin = System.in;
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(GrepException.class, ()->{
            grepApp.run(args, stdin, stdout);
        });
        assertEquals("grep: " + ERR_SYNTAX, thrown.getMessage());
    }
}