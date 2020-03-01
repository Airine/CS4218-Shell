package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

class RegexArgumentTest {
    private RegexArgument regexArgument;
    private static final String HELLO = "hello, ";
    private static final String WORLD = "world";

    @BeforeEach
    void setup(){
        regexArgument = new RegexArgument(HELLO);
        try {
            TestFileUtils.createSomeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void teardown(){
        TestFileUtils.rmCreatedFiles();
    }

    @Test
    void testAppend() {
        char chr = 'c';
        regexArgument.append(chr);
        assertEquals(HELLO + chr, regexArgument.toString());
        assertEquals(Pattern.quote(HELLO) + Pattern.quote(String.valueOf(chr)), regexArgument.getRegex());
    }

    @Test
    void testAppendAsterisk() {
        regexArgument.appendAsterisk();
        assertEquals(HELLO + "*", regexArgument.toString());
        assertEquals(Pattern.quote(HELLO) + "[^" + StringUtils.fileSeparator() + "]*", regexArgument.getRegex());
        assertTrue(regexArgument.isRegex());
    }

    @Test
    void testMergeRegex() {
        RegexArgument argument = new RegexArgument();
        regexArgument.merge(argument);
        assertEquals(HELLO, regexArgument.toString());
        assertEquals(Pattern.quote(HELLO), regexArgument.getRegex());
    }

    @Test
    void testMergeStr() {
        String str = "Mike";
        regexArgument.merge(str);
        assertEquals(HELLO + str, regexArgument.toString());
        assertEquals(Pattern.quote(HELLO) + Pattern.quote(str), regexArgument.getRegex());
    }

    @Test
    void testGlobFilesNotRegex() {
        RegexArgument regexArgument = new RegexArgument(HELLO, WORLD, false);
        List<String> result = regexArgument.globFiles();
        assertEquals(Arrays.asList(WORLD), result);
    }

    @Test
    void testGlobFilesIsRegexAbsolutePath() {
        String text = TestFileUtils.tempFileInFolder;
        String str = "*";
        RegexArgument regexArgument = new RegexArgument(str, text, true);
        List<String> result = regexArgument.globFiles();
        assertEquals(Arrays.asList(TestFileUtils.tempFolderName + CHAR_FILE_SEP + "test.cc"), result);
    }

    @Test
    void testGlobFilesIsRegexRelativePath() {
        String text = "test/temp/test-folder/test.cc";
        String str = "";
        RegexArgument regexArgument = new RegexArgument(str, text, true);
        List<String> result = regexArgument.globFiles();
        assertEquals(Arrays.asList(FileSystemUtils.joinPath("test","temp","test-folder","test.cc")), result);
    }

    @Test
    void testIsRegex() {
        assertEquals(false, regexArgument.isRegex());
    }

    @Test
    void isEmpty() {
        assertEquals(false, regexArgument.isEmpty());
    }

    @Test
    void testToString() {
        assertEquals(HELLO, regexArgument.toString());
    }

}