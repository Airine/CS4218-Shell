package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemUtilsTest {


    @Test
    void joinFilePath() {
        assertEquals("test" + File.separator + "test" + File.separator + "test",
                FileSystemUtils.joinPath("test", "test", "test"));
    }

    @Test
    void joinDirPath() {
        assertEquals("test" + File.separator + "test" + File.separator + "test" + File.separator,
                FileSystemUtils.joinPath(
                        "test" + File.separator, "test" + File.separator, "test" + File.separator));
    }

    @Test
    void joinMixedPath() {
        assertEquals("test" + File.separator + "test" + File.separator + "test",
                FileSystemUtils.joinPath("test", "test/", "test"));
    }

    @Test
    @DisplayName("only works in unix")
    void joinRootPath() {
        assertEquals("/test/test/test", FileSystemUtils.joinPath("/", "test", "test/", "test"));
    }

    @Test
    void isSubDirTestSameFile() {
        assertTrue(FileSystemUtils.isSubDir("home", "home"));
    }

    @Test
    void isSubDirTestChildDir() {
        assertTrue(FileSystemUtils.isSubDir("home", "home/test"));
    }

    @Test
    @DisplayName("it should be sub dir even it is in the second level folder")
    void isSubDirTestChildDir2Level() {
        assertTrue(FileSystemUtils.isSubDir("home", "home/test/test2"));
    }

    @Test
    void isSubDirTestChildDirFalse() {
        assertFalse(FileSystemUtils.isSubDir("home/test", "home"));
    }

    @Test
    void isFileInFolder() {
        assertTrue(FileSystemUtils.isFileInFolder("home", "."));
        assertTrue(FileSystemUtils.isFileInFolder("home", "./"));
        assertTrue(FileSystemUtils.isFileInFolder("home", "./"));
        assertTrue(FileSystemUtils.isFileInFolder("asset/test", "asset/app./../"));
    }

    @Test
    @DisplayName("this is negative test")
    void isFileInFolderFalse() {
        assertTrue(FileSystemUtils.isFileInFolder("./home", "."));
        assertFalse(FileSystemUtils.isFileInFolder("home/test", "./"));
    }

}