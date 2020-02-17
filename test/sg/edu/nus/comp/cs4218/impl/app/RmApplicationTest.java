package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.EnvironmentUtils;
import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class RmApplicationTest {

    RmInterface remove = new RmApplication();

    String currTestDir = joinPath(EnvironmentUtils.currentDirectory, "test", "temp");
    String tempFileName1 = joinPath(currTestDir, "test1.txt");
    String tempFileName2 = joinPath(currTestDir, "test2.txt");
    String tempFolderName = joinPath(currTestDir, "test-folder");
    String tempFolderFName = joinPath(currTestDir, "test-folder/test.cc");
    String emptyFolderName = joinPath(currTestDir, "emptyFolder");


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

    private void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
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
    }


    @AfterEach
    void rmCreatedFiles() {
        deleteFile(tempFileName1);
        deleteFile(tempFileName2);
        File emptyFoler = new File(emptyFolderName);
        if (emptyFoler.exists()) {
            emptyFoler.delete();
        }
        File normalFolder = new File(tempFolderName);
        if (normalFolder.exists()) {
            normalFolder.delete();
        }
    }


    @Test
    void removeOneFile() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, false, tempFileName1);
        } catch (Exception e) {
            fail("should not have exception");
        }
        assertFalse(new File(tempFileName1).exists());
    }

    @Test
    void removeMultipleFile() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, false, tempFileName1, tempFileName2);
        } catch (Exception e) {
            fail("should not have exception");
        }
        assertFalse(new File(tempFileName1).exists());
        assertFalse(new File(tempFileName2).exists());
    }


    @Test
    void removeEmptyFolder() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, true, emptyFolderName);
        } catch (Exception e) {
            fail("should not have exception");
        }
        assertFalse(new File(emptyFolderName).exists());
    }


    @Test
    void removeNoneEmptyFolder() {
        //Boolean isEmptyFolder, Boolean isRecursive, String... fileName
        try {
            remove.remove(false, true, tempFolderName);
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
            remove.remove(true, true, tempFolderName, tempFolderFName);
        } catch (Exception e) {
            return;
        }
        fail("remove folder and file at the same time should throw exception");
    }

    @Test
    void runDeleteOneFile() {
        String[] args = {"rm", tempFileName1};
        try {
            remove.run(args, System.in, System.out);
        } catch (AbstractApplicationException e) {
            e.printStackTrace();
        }
        assertFalse(new File(tempFileName1).exists());
    }

    @Test
    void runDeleteMultipleFile() {
        String[] args = {"rm", tempFileName1, tempFileName2};
        try {
            remove.run(args, System.in, System.out);
        } catch (AbstractApplicationException e) {
            e.printStackTrace();
        }
        assertFalse(new File(tempFileName1).exists());
        assertFalse(new File(tempFileName2).exists());
    }
}