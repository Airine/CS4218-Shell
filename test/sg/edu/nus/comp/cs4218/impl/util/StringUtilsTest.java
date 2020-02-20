package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;


class StringUtilsTest {

    private String str;
    private final Properties props = System.getProperties();
    private String osName;
    private final static String OSNAMESTR = "os.name";

    @BeforeEach
    void storeOSName() {
        osName = System.getProperty(OSNAMESTR);
    }

    @Test
    void nullIsBlank() {
        str = null;
        assertTrue(isBlank(str));
    }

    @Test
    void emptyIsBlank() {
        str = "";
        assertTrue(isBlank(str));
    }

    @Test
    void singleWhitespaceIsBlank() {
        str = " ";
        assertTrue(isBlank(str));
    }

    @Test
    void multiWhitespaceIsBlank() {
        str = "      ";
        assertTrue(isBlank(str));
    }

    @Test
    void normalStringIsNotBlank() {
        str = "test";
        assertFalse(isBlank(str));
    }

    @Test
    void normalCheckingSeparator() {
        System.out.println(System.getProperty(OSNAMESTR));
        assertEquals(System.getProperty("file.separator"), fileSeparator(), "Normal Check");
    }

    @Test
    void macSeparator() {
        props.setProperty(OSNAMESTR, "Mac OS X");
        assertEquals("/", fileSeparator());
    }

    @Test
    void linuxSeparator() {
        props.setProperty(OSNAMESTR, "Linux");
        assertEquals("/", fileSeparator());
    }

    @Test
    void windowsSeparator() {
        props.setProperty(OSNAMESTR, "Windows");
        assertEquals('\\'+"/", fileSeparator());
    }

    @Test
    void tokenizeNormal() {
        String[] result = {"this", "is", "a", "test"};
        assertArrayEquals(result, tokenize("this is a test"));
    }

    @Test
    void tokenizeEmpty() {
        String[] result = new String[0];
        assertArrayEquals(result, tokenize(""));
    }

    @Test
    void zeroIsNumber() {
        assertTrue(isNumber("0"));
    }

    @Test
    void negativeNumberIsNumber() {
        assertTrue(isNumber("-1"));
    }

    @Test
    void bigNumberIsNumber() {
        assertTrue(isNumber("12345686543234566543456654323"));
    }

    @Test
    void manyZeroesIsNumber() {
        assertTrue(isNumber("0000000000000"));
    }

    @Test
    void emptyIsNotNumber() {
        assertFalse(isNumber(""));
    }

    @Test
    void textIsNotNumber() {
        assertFalse(isNumber("test"));
    }

    @Test
    void multiplyCharNormal() {
        assertEquals("aaa", multiplyChar('a', 3));
    }

    @Test
    void multiplyCharNormalEmpty() {
        assertEquals("", multiplyChar('a', 0));
    }


    @Test
    void multiplyCharNormalNegative() {
        assertEquals("", multiplyChar('a', -1));
    }


    @AfterEach
    void resetOSName(){
        props.setProperty(OSNAMESTR, osName);
    }
}