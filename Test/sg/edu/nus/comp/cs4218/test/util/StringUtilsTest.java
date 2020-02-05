package sg.edu.nus.comp.cs4218.test.util;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    private String str;

    @Test
    void nullIsBlank(){
        str = null;
        assertTrue(StringUtils.isBlank(str));
    }

    @Test
    void emptyIsBlank(){
        str = "";
        assertTrue(StringUtils.isBlank(str));
    }

    @Test
    void singleWhitespaceIsBlank(){
        str = " ";
        assertTrue(StringUtils.isBlank(str));
    }

    @Test
    void multiWhitespaceIsBlank(){
        str = "      ";
        assertTrue(StringUtils.isBlank(str));
    }

    @Test
    void normalStringIsNotBlank(){
        str = "test";
        assertFalse(StringUtils.isBlank(str));
    }
}