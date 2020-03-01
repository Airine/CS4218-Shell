package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import static org.junit.jupiter.api.Assertions.*;

class RmArgsParserTest {
    private final String[] recursiveArgs = {"-r", "test1", "test2.txt"};//NOPMD
    private final String[] emptyFolderArgs = {"-d", "test1", "test2.txt"};
    private RmArgsParser parser;

    @BeforeEach
    public void setUp() {
        parser = new RmArgsParser();
    }

    @Test
    void isRecursiveTrue() {
        try {
            parser.parse(recursiveArgs);
        } catch (InvalidArgsException e) {
            fail();
        }
        assertTrue(parser.isRecursive());
    }

    @Test
    void isRecursiveFalse() {
        try {
            parser.parse(emptyFolderArgs);
        } catch (InvalidArgsException e) {
            fail();
        }
        assertFalse(parser.isRecursive());
    }

    @Test
    void isEmptyFolderTrue() {
        try {
            parser.parse(emptyFolderArgs);
        } catch (InvalidArgsException e) {
            fail();
        }
        assertTrue(parser.isEmptyFolder());
    }

    @Test
    void isEmptyFolderFalse() {
        try {
            parser.parse(recursiveArgs);
        } catch (InvalidArgsException e) {
            fail();
        }
        assertFalse(parser.isEmptyFolder());
    }


    @Test
    void invalidFlags() {
        try {
            parser.parse("-f", "test1", "test2");
        } catch (InvalidArgsException e) {
            return;
        }
        fail("not valid flag: -f");
    }

    @Test
    void files() {
        try {
            parser.parse("-r", "test1", "-d", "test2");
        } catch (InvalidArgsException e) {
            fail();
        }
        assertArrayEquals(new String[]{
                FileSystemUtils.getAbsolutePathName("test1"),
                FileSystemUtils.getAbsolutePathName("test2"),
        }, parser.files());
    }
}