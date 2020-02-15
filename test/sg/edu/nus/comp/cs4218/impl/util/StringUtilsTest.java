package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.*;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.fileSeparator;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.isBlank;


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

    @AfterEach
    void resetOSName(){
        props.setProperty(OSNAMESTR, osName);
    }
}