package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.SedInterface;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;


class SedApplicationTest {

    private final SedInterface app = new SedApplication();

    @Test
    void testReplaceEmptyCharater() throws Exception {
        String original = " abcab";
        String pattern = "\\b";
        String replacement = "";
        String expected = "abcab";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceWithEmptyCharater() throws Exception {
        String original = "abcab";
        String pattern = "a";
        String replacement = "";
        String expected = "bcab";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceSingleCharater() throws Exception {
        String original = "abcab";
        String pattern = "a";
        String replacement = "d";
        String expected = "dbcab";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceMultipleCharater() throws Exception {
        String original = "abcab";
        String pattern = "abc";
        String replacement = "d";
        String expected = "dab";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceSecondMatchCharater() throws Exception {
        String original = "abcab";
        String pattern = "a";
        String replacement = "d";
        String expected = "abcdb";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 2, stdin).trim());
    }

    @Test
    void testReplaceDotCharater() throws Exception {
        String original = "aabca";
        String pattern = "a.c";
        String replacement = "d";
        String expected = "ada";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceCaretCharater() throws Exception {
        String original = "abcdabc";
        String pattern = "^abc";
        String replacement = "d";
        String expected = "ddabc";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceDollarCharater() throws Exception {
        String original = "abcdabc";
        String pattern = "abc$";
        String replacement = "d";
        String expected = "abcdd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceStarCharater() throws Exception {
        String original = "abbbbbcd";
        String pattern = "ab*";
        String replacement = "c";
        String expected = "ccd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplacePlusCharater() throws Exception {
        String original = "abbcd";
        String pattern = "ab+";
        String replacement = "c";
        String expected = "ccd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceQuestionCharater() throws Exception {
        String original = "abbcd";
        String pattern = "ac?";
        String replacement = "c";
        String expected = "cbbcd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceSpecialCharater() throws Exception {
        String original = "ab?cd";
        String pattern = "\\?";
        String replacement = "*";
        String expected = "ab*cd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceBraceCharater() throws Exception {
        String original = "abbbbcd";
        String pattern = "ab{2}";
        String replacement = "c";
        String expected = "cbbcd";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceBracketCharater() throws Exception {
        String original = "abcde";
        String pattern = "[b-d]+";
        String replacement = "a";
        String expected = "aae";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceParenthesesCharater() throws Exception {
        String original = "abcabcde";
        String pattern = "(abc)+";
        String replacement = "a";
        String expected = "ade";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }

    @Test
    void testReplaceBarCharater() throws Exception {
        String original = "abcde";
        String pattern = "d|f";
        String replacement = "a";
        String expected = "abcae";
        InputStream stdin = new ByteArrayInputStream(original.getBytes());
        assertEquals(expected, app.replaceSubstringInStdin(pattern, replacement, 1, stdin).trim());
    }
}