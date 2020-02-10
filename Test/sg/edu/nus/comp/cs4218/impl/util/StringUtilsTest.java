package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.*;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.fileSeparator;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.isBlank;


class StringUtilsTest {

    private String str;
    private final Properties props = System.getProperties();
    private String OSNAME;

    @BeforeEach
    void storeOSName() {
        OSNAME = System.getProperty("os.name");
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
        System.out.println(System.getProperty("os.name"));
        assertEquals(System.getProperty("file.separator"), fileSeparator(), "Normal Check");
    }

    @Test
    void macSeparator() {
        props.setProperty("os.name", "Mac OS X");
        assertEquals("/", fileSeparator());
    }

    @Test
    void linuxSeparator() {
        props.setProperty("os.name", "Linux");
        assertEquals("/", fileSeparator());
    }

    @Test
    void windowsSeparator() {
        props.setProperty("os.name", "Windows");
        assertEquals('\\'+"/", fileSeparator());
    }

    @AfterEach
    void resetOSName(){
        props.setProperty("os.name", OSNAME);
    }
}