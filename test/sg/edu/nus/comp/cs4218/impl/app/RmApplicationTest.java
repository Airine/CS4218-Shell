package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.EnvironmentUtils;
import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class RmApplicationTest {

    private final RmInterface remove = new RmApplication();

    private final String currTestDir = joinPath(EnvironmentUtils.currentDirectory, "test", "temp");
    private final String tempFileName1 = joinPath(currTestDir, "test1.txt");
    private final String tempFileName2 = joinPath(currTestDir, "test2.txt");
    private final String tempFolderName = joinPath(currTestDir, "test-folder");
    private final String tempFileInFolder = joinPath(currTestDir, "test-folder/test.cc");
    private final String emptyFolderName = joinPath(currTestDir, "emptyFolder");


    private static String joinPath(String... fileFolderName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileFolderName[0]);
        for (int i = 1; i < fileFolderName.length; i++) {
            stringBuilder.append(File.separator).append(fileFolderName[i]);
        }
        return stringBuilder.toString();
    }

    private void createTestFile(String tempFileName) throws Exception {
        File file = new File(tempFileName);
        if (file.exists()) {
            throw new Exception("test terminated, this test file already exist!" + tempFileName);
        } else {
            if (!file.createNewFile()) {
                throw new Exception("create file failed");
            }
        }
    }


    @BeforeEach
    void createSomeFiles() throws Exception {
        File testFolder = new File(currTestDir);
        if (!testFolder.exists()) {
            testFolder.mkdirs();
        }
        createTestFile(tempFileName1);
        createTestFile(tempFileName2);
        File emptyDir = new File(emptyFolderName);
        if (!emptyDir.exists()) {
            emptyDir.mkdirs();
        }

        File folder = new File(tempFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        createTestFile(tempFileInFolder);
    }


    @AfterEach
    void rmCreatedFiles() {
        FileSystemUtils.deleteFileRecursive(new File(currTestDir));
    }


    @Test
    void removeOneFile() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, false, tempFileName1);
        } catch (Exception e) {
            fail("remove one file should not have exception");
        }
        assertFalse(new File(tempFileName1).exists());
    }

    @Test
    void removeMultipleFile() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, false, tempFileName1, tempFileName2);
        } catch (Exception e) {
            fail("remove multiple files should not have exception");
        }
        assertFalse(new File(tempFileName1).exists());
        assertFalse(new File(tempFileName2).exists());
    }


    @Test
    void removeEmptyFolderFail() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, false, emptyFolderName);
        } catch (Exception e) {
            return;
        }
        fail("should have exception");
    }

    /**
     * this should be success, test rm -d [empty folder name]
     */
    @Test
    void removeEmptyFolderSuccess() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(true, false, emptyFolderName);
        } catch (Exception e) {
            fail("should not have exception");
        }
        assertFalse(new File(emptyFolderName).exists());
    }


    @Test
    void removeNoneEmptyFolderFail() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, false, tempFolderName);
        } catch (Exception e) {
            return;
        }
        try {
            remove.remove(true, false, tempFolderName);
        } catch (Exception e) {
            return;
        }
        fail("remove none empty folder with option isEmptyFolder true should throw exception");
    }

    @Test
    void removeNoneEmptyFolderSuccess() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, true, tempFolderName);
        } catch (Exception e) {
            fail("remove none empty folder with option isEmptyFolder false should not throw exception");
        }
        assertFalse(new File(tempFolderName).exists());
    }

    @Test
    void removeNoneExistFile() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, true, tempFolderName + "non_exist");
        } catch (Exception e) {
            return;
        }
        fail("remove none exist file should throw exception");
    }

    @Test
    void removeNoneEmptyFolderWithEmptyOption() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(true, false, tempFolderName);
        } catch (Exception e) {
            return;
        }
        fail("remove none empty folder with option isEmptyFolder true should throw exception");
    }

    @Test
    void removeFileAndFolder() {
        // should throw exception because we can not have command rm test test.txt whatever option it has
        // it is different in linux, linux use rm -rf could deal with, however, in our case, we do not have option -f
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(true, true, tempFileName1, tempFileInFolder);
        } catch (Exception e) {
            fail("remove folder and file at the same time should not throw exception");
        }
    }

    @Test
    void runDeleteOneFile() {
        String[] args = {tempFileName1};
        try {
            remove.run(args, System.in, System.out);
        } catch (AbstractApplicationException e) {
            e.printStackTrace();
        }
        assertFalse(new File(tempFileName1).exists());
    }

    @Test
    void runDeleteMultipleFile() {
        String[] args = {tempFileName1, tempFileName2};
        try {
            remove.run(args, System.in, System.out);
        } catch (AbstractApplicationException e) {
            e.printStackTrace();
        }
        assertFalse(new File(tempFileName1).exists());
        assertFalse(new File(tempFileName2).exists());
    }

    @Test
    void runDeleteFolderRecursively() {
        String[] args = {"-r", tempFolderName, tempFileName2};
        try {
            remove.run(args, System.in, System.out);
        } catch (AbstractApplicationException e) {
            e.printStackTrace();
        }
        assertFalse(new File(tempFolderName).exists());
        assertFalse(new File(tempFileName2).exists());
    }

    @Test
    void runDeleteEmptyFolder() {
        String[] args = {"-d", tempFileName1, emptyFolderName};
        try {
            remove.run(args, System.in, System.out);
        } catch (AbstractApplicationException e) {
            e.printStackTrace();
        }
        assertFalse(new File(tempFileName1).exists());
        assertFalse(new File(emptyFolderName).exists());
    }

    @Test
    void runDeleteWithMultipleOption1() {
        String[] args = {"-d", emptyFolderName, "-r", tempFolderName};
        try {
            remove.run(args, System.in, System.out);
        } catch (AbstractApplicationException e) {
            e.printStackTrace();
        }
        assertFalse(new File(tempFolderName).exists());
        assertFalse(new File(emptyFolderName).exists());
    }

    @Test
    void runDeleteWithMultipleOption2() {
        String[] args = {"-r", tempFolderName, "-d", emptyFolderName,};
        try {
            remove.run(args, System.in, System.out);
        } catch (AbstractApplicationException e) {
            e.printStackTrace();
        }
        assertFalse(new File(tempFolderName).exists());
        assertFalse(new File(emptyFolderName).exists());
    }

    @Test
    void runDeleteEmptyFile() {
        String[] args = {"nonexist"};
        OutputStream outputStream = null; //NOPMD their are special method to close it
        InputStream inputStream = null;//NOPMD
        try {
            outputStream = IOUtils.openOutputStream(tempFileName1);
            inputStream = IOUtils.openInputStream(tempFileName1);

            assertTrue(inputStream.read() < 0);
            remove.run(args, System.in, outputStream);
            assertTrue(inputStream.read() > 0);
        } catch (AbstractApplicationException | ShellException | IOException e) {
            fail("should not have exception" + e.getMessage());
        } finally {
            try {
                IOUtils.closeOutputStream(outputStream);
                IOUtils.closeInputStream(inputStream);
            } catch (ShellException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void runExceptionWriteToClosedStream() {
        String[] args = {"nonexist"};
        try {
            OutputStream outputStream = IOUtils.openOutputStream(tempFileName1); //NOPMD close stream early
            IOUtils.closeOutputStream(outputStream);
            remove.run(args, System.in, outputStream);
        } catch (AbstractApplicationException | ShellException e) {
            return;
        }
        fail("write error message to closed stream should appear exception");
    }

}