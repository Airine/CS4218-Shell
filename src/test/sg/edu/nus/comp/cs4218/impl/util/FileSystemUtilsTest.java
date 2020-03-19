package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemUtilsTest {

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
}