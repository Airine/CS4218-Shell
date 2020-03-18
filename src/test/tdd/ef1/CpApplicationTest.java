package tdd.ef1;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.impl.app.CpApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class CpApplicationTest {

    CpApplication cpApplication;
    InputStream inputStream = mock(InputStream.class);
    OutputStream outputStream = mock(OutputStream.class);
    private String FILENAME1 = "file1.txt";
    private File file1 = new File(FILENAME1);
    private String FILENAME2 = "file2.txt";
    private File file2 = new File(FILENAME2);
    private String file1Data = "hello world";
    private String file2Data = "lol more stuff here";

    @BeforeEach
    void setUp() {
        cpApplication = new CpApplication();
        try {
            file1.createNewFile();
            file2.createNewFile();
            FileWriter fw1 = new FileWriter(FILENAME1);
            fw1.write(file1Data);
            fw1.close();
            FileWriter fw2 = new FileWriter(FILENAME2);
            fw2.write(file2Data);
            fw2.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        file1.delete();
        file2.delete();
    }

    private void assertFileContentsEqual(String expected, Path path) {
        String actual = null;
        try {
            actual = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should throw error if null args")
    void throwsExceptionNullArgs() {
        assertThrows(CpException.class, () -> cpApplication.run(null, inputStream, outputStream));
    }

    @Test
    @DisplayName("should throw error if not enough args")
    void throwsExceptionNotEnoughArgs() {
        assertThrows(CpException.class, () -> cpApplication.run(new String[]{"src.txt"}, inputStream, outputStream));
    }

    @Nested
    @DisplayName("cpSrcFileToDestFileTest")
    class cpSrcFileToDestFileTests {

        @Test
        @DisplayName("should throw exception if invalid src file")
        void throwsExceptionSrcFileNotFound() {
            assertThrows(CpException.class, () -> cpApplication.run(new String[]{"src.txt", "dest.txt"},
                    inputStream, outputStream));
        }

        @Test
        @DisplayName("should copy contents of one file to another")
        void copySrcFileToDest() throws AbstractApplicationException {
            cpApplication.run(new String[]{FILENAME1, FILENAME2}, inputStream, outputStream);
            assertFileContentsEqual(file1Data, file1.toPath());
            assertFileContentsEqual(file1Data, file2.toPath());
        }

        @Test
        @DisplayName("should create dest file if nonexistent and copy contents of src")
        void copySrcFileToDestandCreateDest() throws AbstractApplicationException {
            String destFile = "dest.txt";
            cpApplication.run(new String[]{FILENAME1, destFile}, inputStream, outputStream);

            assertFileContentsEqual(file1Data, file1.toPath());
            assertFileContentsEqual(file1Data, Paths.get(destFile));
            new File(destFile).delete();
        }
    }

    @Nested
    @DisplayName("CpFilesToFolderTests")
    class CpFilesToFolderTests {
        private String DIR_NAME = "destDir";
        private File destDirectory = new File(DIR_NAME);
        private File fileInDestDirectory = new File(destDirectory, FILENAME2);
        private String fileInDestData = "different content";

        @BeforeEach
        void setUp() {
            try {
                destDirectory.mkdir();
                fileInDestDirectory.createNewFile();
                FileWriter fw = new FileWriter(fileInDestDirectory);
                fw.write(fileInDestData);
                fw.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @AfterEach
        void tearDown() {
            for (File f : destDirectory.listFiles()) {
                f.delete();
            }
            destDirectory.delete();
        }

        @Test
        @DisplayName("should throw exception when invalid src file but copy the rest")
        void throwExceptionInvalidSrcFilesAndCopyValidOnes() {
            assertThrows(CpException.class, () -> cpApplication.run(new String[]{FILENAME1, "invalid file name",
                    DIR_NAME}, inputStream, outputStream));

            assertFileContentsEqual(file1Data, file1.toPath());
            assertFileContentsEqual(file1Data, Paths.get(DIR_NAME, FILENAME1));
            assertFileContentsEqual(fileInDestData, fileInDestDirectory.toPath());
        }

        @Test
        @DisplayName("should copy one file to target folder")
        void copyFileToDestFolder() throws AbstractApplicationException {
            cpApplication.run(new String[]{FILENAME1, DIR_NAME}, inputStream, outputStream);

            assertFileContentsEqual(file1Data, file1.toPath());
            assertFileContentsEqual(file1Data, Paths.get(DIR_NAME, FILENAME1));
            assertFileContentsEqual(fileInDestData, fileInDestDirectory.toPath());
        }

        @Test
        @DisplayName("should copy multiple file to target folder")
        void copyMultiFilesToDestFolder() throws AbstractApplicationException {
            cpApplication.run(new String[]{FILENAME1, FILENAME2, DIR_NAME}, inputStream, outputStream);

            assertFileContentsEqual(file1Data, file1.toPath());
            assertFileContentsEqual(file1Data, Paths.get(DIR_NAME, FILENAME1));
            assertFileContentsEqual(file2Data, fileInDestDirectory.toPath());
        }
    }
}