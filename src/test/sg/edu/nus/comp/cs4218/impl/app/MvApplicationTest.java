package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MvApplicationTest {

    private final MvInterface mvInterface = new MvApplication();
    private String notReadFile;
    private String notReadFolder;

    private String notWriteFile;
    private String notWriteFolder;

    @BeforeEach
    void setUp() {
        try {
            TestFileUtils.createSomeFiles();
            File notReadFolder = TestFileUtils.createFileUnderRootDir("notReadFolder" + File.separator);
            File notReadFile = TestFileUtils.createFileUnderRootDir("notReadFile");
            File notWriteFolder = TestFileUtils.createFileUnderRootDir("notWriteFolder" + File.separator);
            File notWriteFile = TestFileUtils.createFileUnderRootDir("notWriteFile");
            notReadFile.setReadable(false);
            notReadFolder.setReadable(false);
            this.notReadFile = notReadFile.getAbsolutePath();
            this.notReadFolder = notReadFolder.getAbsolutePath();

            notWriteFile.setWritable(false);
            notWriteFolder.setWritable(false);
            this.notWriteFile = notWriteFile.getAbsolutePath();
            this.notWriteFolder = notWriteFolder.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        TestFileUtils.rmCreatedFiles();
    }

    /**
     * test case for milestone 3
     */

    @Test
    @Disabled
    @DisplayName("move to a folder that do not have a read permission")
    void mvNotReadPermitFile() {
        assertDoesNotThrow(() -> mvInterface.mvFilesToFolder(notReadFolder, notReadFile));

        String targetPath = FileSystemUtils.joinPath(notReadFolder, new File(notReadFile).getName());
        assertFalse(new File(notReadFile).exists());
        assertTrue(new File(targetPath).exists());
    }

    @Test
    @DisplayName("move to a folder that do not have a write permission")
    void mvNotWritePermitFile() {
        Throwable t = assertThrows(MvException.class,
                () -> mvInterface.mvFilesToFolder(notWriteFolder, notWriteFile)
        );
        assertTrue(t.getMessage().contains(ErrorConstants.ERR_NO_PERM));
        assertTrue(new File(notWriteFile).exists());
    }


    @Test
    void testMvFileToFolder() {
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName,
                new File(TestFileUtils.tempFileName1).getName());
        assertDoesNotThrow(() -> mvInterface.mvFilesToFolder(TestFileUtils.emptyFolderName, TestFileUtils.tempFileName1));
        assertTrue(new File(destFilePath).exists());
        assertFalse(new File(TestFileUtils.tempFileName1).exists());
    }

    @Test
    void testMvFileToFile() {
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName, "new-file");
        assertDoesNotThrow(() -> mvInterface.mvSrcFileToDestFile(TestFileUtils.tempFileName1, destFilePath));
        assertTrue(new File(destFilePath).exists());
        assertFalse(new File(TestFileUtils.tempFileName1).exists());
    }

    @Test
    void testMvNotExistFileToFile() {
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName, "new-file");
        Throwable throwable = assertThrows(Exception.class, () -> mvInterface.mvSrcFileToDestFile("not-exist", destFilePath));
        assertNotEquals(NullPointerException.class, throwable.getClass());
    }

    @Test
    void testMvNotExistFileToFolder() {
        Throwable throwable = assertThrows(Exception.class, () -> mvInterface.mvFilesToFolder(TestFileUtils.emptyFolderName, "not-exist"));
        assertNotEquals(NullPointerException.class, throwable.getClass());
    }

    @Test
    void testMvToNotExistFolder() {
        String destFolder = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName, "no_exist_folder");
        Throwable throwable = assertThrows(Exception.class,
                () -> mvInterface.mvFilesToFolder(destFolder, TestFileUtils.tempFileName1));
        assertNotEquals(NullPointerException.class, throwable.getClass());
    }

    @Test
    void runMvNotArgument() {
        String[] args = {};
        try (NewIOStream ioStream = new NewIOStream(TestFileUtils.tempFileName1, TestFileUtils.tempFileName1)) {
            assertTrue(ioStream.inputStream.read() < 0);
            assertDoesNotThrow(() -> mvInterface.run(args, System.in, ioStream.outputStream));
            assertTrue(ioStream.inputStream.read() > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void runMvWithOneArgument() {
        String[] args = {TestFileUtils.tempFolderName};
        try (NewIOStream ioStream = new NewIOStream(TestFileUtils.tempFileName1, TestFileUtils.tempFileName1)) {
            assertTrue(ioStream.inputStream.read() < 0);
            assertDoesNotThrow(() -> mvInterface.run(args, System.in, ioStream.outputStream));
            assertTrue(ioStream.inputStream.read() > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void runMvMultipleFileToFile() {
        String[] args = {TestFileUtils.tempFolderName, TestFileUtils.tempFileName1, TestFileUtils.tempFileName2};
        try (NewIOStream ioStream = new NewIOStream(TestFileUtils.tempFileName1, TestFileUtils.tempFileName1)) {
            assertTrue(ioStream.inputStream.read() < 0);
            assertDoesNotThrow(() -> mvInterface.run(args, System.in, ioStream.outputStream));
            assertTrue(ioStream.inputStream.read() > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void runMvWriteCloseStream() {
        String[] args = {TestFileUtils.tempFolderName};
        try (NewIOStream ioStream = new NewIOStream(TestFileUtils.tempFileName1, TestFileUtils.tempFileName1)) {
            ioStream.outputStream.close();
            assertThrows(MvException.class, () -> mvInterface.run(args, System.in, ioStream.outputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void runMvToFolder() {
        String[] args = {TestFileUtils.tempFileName1, TestFileUtils.emptyFolderName};
        assertDoesNotThrow(() -> mvInterface.run(args, System.in, System.out));
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName,
                new File(TestFileUtils.tempFileName1).getName());
        assertTrue(new File(destFilePath).exists());
        assertFalse(new File(TestFileUtils.tempFileName1).exists());
    }

    @Test
    void runMvToNewFile() {
        String destFilePath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName, "new-file");
        String[] args = {TestFileUtils.tempFileName1, destFilePath};
        assertDoesNotThrow(() -> mvInterface.run(args, System.in, System.out));
        assertTrue(new File(destFilePath).exists());
        assertFalse(new File(TestFileUtils.tempFileName1).exists());
    }

    @Test
    void runMvFolderToExistFile() {
        String destPathName = FileSystemUtils.joinPath(TestFileUtils.tempFileName2);
        String[] args = {TestFileUtils.emptyFolderName, destPathName};

        try (NewIOStream ioStream = new NewIOStream(TestFileUtils.tempFileName1, TestFileUtils.tempFileName1)) {
            assertTrue(ioStream.inputStream.read() < 0);
            assertDoesNotThrow(() -> mvInterface.run(args, System.in, ioStream.outputStream));
            assertTrue(ioStream.inputStream.read() > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void runMvFolderToExistFileWithFlags() {
        String destPathName = FileSystemUtils.joinPath(TestFileUtils.tempFileName2);
        String[] args = {"-n", TestFileUtils.emptyFolderName, destPathName};

        assertDoesNotThrow(() -> mvInterface.run(args, System.in, System.out));
        assertTrue(new File(TestFileUtils.emptyFolderName).exists());
    }

    @Test
    void runMvFolderToFolder() {
        String[] args = {TestFileUtils.tempFolderName, TestFileUtils.emptyFolderName};
        String destFolderPath = FileSystemUtils.joinPath(TestFileUtils.emptyFolderName,
                new File(TestFileUtils.tempFolderName).getName());
        assertDoesNotThrow(() -> mvInterface.run(args, System.in, System.out));
        assertTrue(new File(destFolderPath).exists());
        assertTrue(new File(destFolderPath).isDirectory());
        assertFalse(new File(TestFileUtils.tempFolderName).exists());
    }


}