package ef1;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.impl.app.CpApplication;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class CpApplicationTest {

    private CpInterface cpInterface = new CpApplication();

    @BeforeEach
    void setUp() {
        try {
            TestFileUtils.createSomeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        TestFileUtils.rmCreatedFiles();
    }

    @Test
    public void testCpFileToFolder() {
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName,
                new File(TestFileUtils.tempFileName1).getName());
        assertDoesNotThrow(() -> cpInterface.cpFilesToFolder(TestFileUtils.emptyFolderName, TestFileUtils.tempFileName1));
        assertTrue(new File(destFilePath).exists());
    }

    @Test
    public void testCpFileToFile() {
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName, "new-file");
        assertDoesNotThrow(() -> cpInterface.cpSrcFileToDestFile(TestFileUtils.tempFileName1, destFilePath));
        assertTrue(new File(destFilePath).exists());
    }

    @Test
    public void testCpNotExistFileToFile() {
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName, "new-file");
        Throwable throwable = assertThrows(Exception.class, () -> cpInterface.cpSrcFileToDestFile("not-exist", destFilePath));
        assertNotEquals(NullPointerException.class, throwable.getClass());
    }

    @Test
    public void testCpNotExistFileToFolder() {
        Throwable throwable = assertThrows(Exception.class, () -> cpInterface.cpFilesToFolder(TestFileUtils.emptyFolderName, "not-exist"));
        assertNotEquals(NullPointerException.class, throwable.getClass());
    }

    @Test
    public void testCpToNotExistFolder() {
        String destFolder = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName, "no_exist_folder");
        Throwable throwable = assertThrows(Exception.class,
                () -> cpInterface.cpFilesToFolder(destFolder, TestFileUtils.tempFileName1));
        assertNotEquals(NullPointerException.class, throwable.getClass());
    }

    @Test
    public void runCpToFolder() {
        String[] args = {TestFileUtils.tempFileName1, TestFileUtils.emptyFolderName};
        assertDoesNotThrow(() -> cpInterface.run(args, System.in, System.out));
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName,
                new File(TestFileUtils.tempFileName1).getName());
        assertTrue(new File(destFilePath).exists());
    }

    @Test
    public void runCpToNewFile() {
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName, "new-file");
        String[] args = {TestFileUtils.tempFileName1, destFilePath};
        assertDoesNotThrow(() -> cpInterface.run(args, System.in, System.out));
        assertTrue(new File(destFilePath).exists());
    }

    @Test
    @DisplayName("copy a folder to an exist file should throw exception")
    public void runCpFolderToExistFile() {
        String[] args = {TestFileUtils.emptyFolderName, TestFileUtils.tempFileName1};
        assertDoesNotThrow(() -> cpInterface.run(args, System.in, System.out));
        assertTrue(new File(TestFileUtils.emptyFolderName).isDirectory());
        assertTrue(new File(TestFileUtils.tempFileName1).isFile());
    }

    @Test
    @DisplayName("copy a non existing file should throw exception")
    public void runCpNonExistFolderToFile() {
        String[] args = {"no_exist", TestFileUtils.tempFileName1};
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> cpInterface.run(args, System.in, outputStream));
        assertTrue(new File(TestFileUtils.tempFileName1).isFile());
        assertFalse(outputStream.toString().isEmpty());
    }


    @Test
    public void runCpFolderToFolder() {
        String[] args = {TestFileUtils.tempFolderName, TestFileUtils.emptyFolderName};
        String destFolderPath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName,
                new File(TestFileUtils.tempFolderName).getName());
        assertDoesNotThrow(() -> cpInterface.run(args, System.in, System.out));
        assertTrue(new File(destFolderPath).exists());
        assertTrue(new File(destFolderPath).isDirectory());
    }

    @Test
    @DisplayName("similar to cp temp/ temp/")
    public void copyFolderToItself() {
        String[] args = {TestFileUtils.tempFolderName, TestFileUtils.tempFolderName};
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> cpInterface.run(args, System.in, outputStream));
        assertTrue(new File(TestFileUtils.tempFolderName).exists());
        assertFalse(outputStream.toString().isEmpty());
    }

    @Test
    @DisplayName("similar to cp temp/test1 temp/test1")
    public void copyFileToItself() {
        String[] args = {TestFileUtils.tempFileName1, TestFileUtils.tempFileName1};
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> cpInterface.run(args, System.in, outputStream));
        assertTrue(new File(TestFileUtils.tempFileName1).exists());
        assertFalse(outputStream.toString().isEmpty());
    }

    @Test
    @DisplayName("similar to cp temp/test1 temp")
    public void copyFileToItselfByDir() {
        String[] args = {TestFileUtils.tempFileName1, "temp"};
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> cpInterface.run(args, System.in, outputStream));
        assertTrue(new File(TestFileUtils.tempFileName1).exists());
    }

}