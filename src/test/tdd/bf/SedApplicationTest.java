package tdd.bf;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.impl.app.SedApplication;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class SedApplicationTest {
    private SedApplication sedApplication;
    private InputStream stdin;
    private OutputStream stdout;
    private static final String TEXT1 = "Hello testing! Hello testing!" + System.lineSeparator() + "Hello Software testing! Hello Software testing!" + System.lineSeparator();
    private static final String REPLACED_FIRST = "Hello tests! Hello testing!" + System.lineSeparator() + "Hello Software tests! Hello Software testing!" + System.lineSeparator();
    private static final String REPLACED_SECOND = "Hello testing! Hello tests!" + System.lineSeparator() + "Hello Software testing! Hello Software tests!" + System.lineSeparator();
    private static final String DELETE_TEXT = " testing! Hello testing!" + System.lineSeparator() + " Software testing! Hello Software testing!" + System.lineSeparator();
    private File dir;
    private File file1;
    private File file2;
    private static final String DIR_NAME = "sedTestDir";
    private static final String FILENAME1 = "sedFile1";
    private static final String FILENAME2 = "sedFile2";
    private String expected;

    @BeforeEach
    public void setUp() throws Exception{
        String currentDir = Environment.currentDirectory;
        dir = new File(currentDir + File.separator + DIR_NAME);
        dir.mkdir();

        file1 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME1);
        Files.write(file1.toPath(), TEXT1.getBytes());
        file2 = new File(currentDir + File.separator + DIR_NAME + File.separator + FILENAME2);
        Files.write(file2.toPath(), TEXT1.getBytes());

        stdin = new ByteArrayInputStream(TEXT1.getBytes());
        stdout = new ByteArrayOutputStream();
        sedApplication = new SedApplication();
    }

    @AfterEach
    public void tearDown() throws Exception {
        stdin.close();
        file1.delete();
        file2.delete();
        dir.delete();
        stdin.close();
        stdout.close();
    }

    @Test
    public void testEmptyRegexStdin() throws Exception {
        try{
            String regexp = "";
            String replacement = "";
            int replacementIndex = 1;
        }catch (Exception expected){
            assertEquals(String.format("ERR_EMPTY_REGEX"), expected.getMessage());
        }
    }

    @Test
    public void testRegexMatchAddLeadBracketStdin() throws Exception {
        expected = "> Hello testing! Hello testing!" + System.lineSeparator() + "> Hello Software testing! Hello Software testing!" + System.lineSeparator();
        String pattern = "^";
        String replacement = "> ";
        int replacementIndex = 1;
        assertEquals(expected, sedApplication.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }

    @Test
    public void testNothingToReplaceStdin() throws Exception {
        String pattern = ">";
        String replacement = "";
        int replacementIndex = 1;
        assertEquals(TEXT1, sedApplication.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }

    @Test
    public void testNeverReachReplacementIndexStdin() throws Exception {
        String pattern = "ab";
        String replacement = "cd";
        int replacementIndex = 50;
        assertEquals(TEXT1, sedApplication.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }

    @Test
    public void testReachReplacementIndexStdin() throws Exception {
        String pattern = "ing";
        String replacement = "s";
        int replacementIndex = 2;
        assertEquals(REPLACED_SECOND, sedApplication.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }

    @Test
    public void testEmptyReplacementStdin() throws Exception {
        String pattern = "Hello";
        String replacement = "";
        int replacementIndex = 1;
        assertEquals(DELETE_TEXT, sedApplication.replaceSubstringInStdin(pattern, replacement, replacementIndex, stdin));
    }


    @Test
    public void testEmptyRegexFile() throws Exception {
        String pattern = "";
        String replacement = "> ";
        int replacementIndex = 1;
        assertEquals(TEXT1, sedApplication.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

    @Test
    public void testRegexMatchFile() throws Exception {
        expected = "> Hello testing! Hello testing!" + System.lineSeparator() + "> Hello Software testing! Hello Software testing!" + System.lineSeparator();
        String pattern = "^";
        String replacement = "> ";
        int replacementIndex = 1;
        assertEquals(expected, sedApplication.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

    @Test
    public void testNothingToReplaceFile() throws Exception {
        String pattern = ">";
        String replacement = "";
        int replacementIndex = 1;
        assertEquals(TEXT1, sedApplication.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

    @Test
    public void testNeverReachReplacementIndexFile() throws Exception {
        String pattern = "ab";
        String replacement = "cd";
        int replacementIndex = 50;
        assertEquals(TEXT1, sedApplication.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

    @Test
    public void testReachReplacementIndexFile() throws Exception {
        String pattern = "ing";
        String replacement = "s";
        int replacementIndex = 2;
        assertEquals(REPLACED_SECOND, sedApplication.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

    @Test
    public void testEmptyReplacementFile() throws Exception {
        String pattern = "Hello";
        String replacement = "";
        int replacementIndex = 1;
        assertEquals(DELETE_TEXT, sedApplication.replaceSubstringInFile(pattern, replacement, replacementIndex, file1.toString()));
    }

    @Test
    public void testFileNotExists() throws Exception {
        try {
            String pattern = "Hello";
            String replacement = "";
            int replacementIndex = 0;
            sedApplication.replaceSubstringInFile(pattern, replacement, replacementIndex, "sedFile3.txt");
        }catch (Exception expected){
            assertEquals(String.format(ERR_FILE_NOT_FOUND), expected.getMessage());
        }
    }

    @Test
    public void testFileIsDirectory() throws Exception {
        try {
            String pattern = "is";
            String replacement = "";
            int replacementIndex = 0;
            sedApplication.replaceSubstringInFile(pattern, replacement, replacementIndex, dir.toString());
        }catch (Exception expected){
            assertEquals(String.format(ERR_IS_DIR), expected.getMessage());
        }
    }

    @Test
    public void testReplacementRuleMissing() throws Exception {
        try {
            String path = file1.toString();
            sedApplication.run(new String[]{path}, System.in, System.out);
        }catch (SedException expected){
            assertEquals("sed: " + String.format(ERR_INVALID_REP_RULE) , expected.getMessage());
        }
    }

    @Test
    public void testInvalidSynaxWithoutS() throws Exception {
        try {
            String path = file1.toString();
            sedApplication.run(new String[]{"/\\t/ /", path}, System.in, System.out);
        }catch (SedException expected){
            assertEquals("sed: " + String.format(ERR_INVALID_REP_RULE), expected.getMessage());
        }
    }

    @Test
    public void testReplacementRuleNegativeIndex() throws Exception {
        try {
            String path = file1.toString();
            sedApplication.run(new String[]{"s/hello/hi/-2", path}, System.in, System.out);
        }catch (SedException expected){
            assertEquals("sed: " + String.format(ERR_INVALID_REP_X), expected.getMessage());
        }
    }

    @Test
    public void testNoStdInAndNoInputFile() throws Exception {
        try {
            String[] args = {"s/test/t/"};
            sedApplication.run(args, null, stdout);
        }catch (SedException expected){
            assertEquals("sed: " + String.format(ERR_NULL_STREAMS) , expected.getMessage());
        }
    }

    @Test
    public void testInvalidSyntaxMissSeparateSymbol() throws Exception {
        try {
            String[] args = {"s/hello", file1.toString()};
            sedApplication.run(args, null, stdout);
        }catch (SedException expected){
            assertEquals("sed: " + String.format(ERR_INVALID_REP_RULE), expected.getMessage());
        }
    }

    @Test
    public void testStdoutMissing() throws Exception {
        try {
            String[] args = {"s/ing/s/", file1.toString()};
            sedApplication.run(args, null, null);
        }catch (SedException expected){
            assertEquals("sed: " + String.format(ERR_NULL_STREAMS), expected.getMessage());
        }
    }


    @Test
    public void testEmptyArguments() throws Exception {
        try {
            String[] args = {};
            sedApplication.run(args, null, stdout);
        }catch (SedException expected){
            assertEquals("sed: "+String.format(ERR_NO_REP_RULE), expected.getMessage());
        }
    }

    @Test
    public void testInvalidReplacementIndex() throws Exception {
        try {
            String[] args = {"s/ing/s/??", file1.toString()};
            sedApplication.run(args, null, stdout);
        }catch (Exception expected){
            assertEquals("sed: " + String.format(ERR_INVALID_REP_X), expected.getMessage());
        }
    }

    @Test
    public void testNoReplacementIndex() throws Exception {
        String[] args = {"s/ing/s/", file1.toString()};
        sedApplication.run(args, null, stdout);
        assertEquals(REPLACED_FIRST, stdout.toString());
    }

    @Test
    public void testNoRegex() throws Exception {
        try {
            String[] args = {"s//s/", file1.toString()};
            sedApplication.run(args, null, stdout);
            assertEquals(TEXT1, stdout.toString());
        }catch(SedException expected){
            assertEquals("sed: " + String.format(ERR_EMPTY_REGEX) , expected.getMessage());
        }
    }

    @Test
    public void testOtherSeparateSymbolFile() throws Exception {
        String[] args = {"sxingxsx", file1.toString()};
        sedApplication.run(args, null, stdout);
        assertEquals(REPLACED_FIRST, stdout.toString());
    }

    @Test
    public void testOtherSeparateSymbol() throws Exception {
        String[] args = {"s|ing|s|"};
        sedApplication.run(args, stdin, stdout);
        assertEquals(REPLACED_FIRST, stdout.toString());
    }




}