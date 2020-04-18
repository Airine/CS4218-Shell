package tdd.ef2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.app.MvApplication;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class MvApplicationTest {

    private static final String BASE_URL = Environment.currentDirectory;
    private static final String EXT = ".txt";
    private static final String TEMP_FILE_A = "tempFileA";
    private static final String TEMP_FILE_B = "tempFileB";
    private static final String TEMP_DIR_A = "tempDirA";
    private static final String TEMP_DIR_B = "tempDirB";
    private static final String NO_OVERRIDE_FLAG = "-n";

    private static final Path CWD = Paths.get(BASE_URL);
    private Path tempFileA;
    private Path tempFileB;
    private Path tempDirA;
    private Path tempDirB;

    private InputStream inputStream;
    private ByteArrayOutputStream outputStream;

    private MvApplication mvApplication;

    @BeforeEach
    void setUp() throws IOException {
        tempFileA = Files.createTempFile(CWD, TEMP_FILE_A, EXT);
        tempFileB = Files.createTempFile(CWD, TEMP_FILE_B, EXT);
        tempDirA = Files.createTempDirectory(CWD, TEMP_DIR_A);
        tempDirB = Files.createTempDirectory(CWD, TEMP_DIR_B);

        mvApplication = new MvApplication();
        inputStream = mock(InputStream.class);
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() throws IOException {
        delete(tempFileA, false);
        delete(tempFileB, false);
        delete(tempDirA, true);
        delete(tempDirB, true);
    }

    // Test mv app with all null parameters
    @Test
    void testAllNullParameters() {
        // mv
        try {
            mvApplication.run(null, null, null);
            fail();
        } catch (MvException e) {
            assertEquals("mv: Null arguments", e.getMessage());
        }
    }

    // Test mv app with all null inputstream, does not fail since not required
    @Test
    void testNullInputStream() {
        // mv tempFileA.txt tempFileB.txt
        try {
            String[] args = {getFileName(tempFileA), getFileName(tempFileB)};
            mvApplication.run(args, null, outputStream);
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app with null outputstream
    @Test
    void testNullOutputStream() {
        // mv
        try {
            String[] args = {};
            mvApplication.run(args, inputStream, null);
            fail();
        } catch (MvException e) {
            assertEquals("mv: OutputStream not provided", e.getMessage());
        }
    }

    // Test mv app with invalid argument
    @Test
    void testWithInvalidArgs() {
        try {
            String[] args = {"-x", getFileName(tempFileB)};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            assertEquals("mv: illegal option -- x", e.getMessage());
        }
    }

    // Test ls app whether output exception is thrown when there is an IOException
    @Test
    void testWritingResultToOutputStreamException() {
        // mv tempFileA.txt tempFileB.txt
        String src = getFileName(tempFileA);
        String dest = getFileName(tempFileB);

        try {
            OutputStream baos = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    throw new IOException();
                }
            };
            String[] args = {"-n", src, dest};
            mvApplication.run(args, inputStream, baos);
            fail("Exception expected");
        } catch (MvException e) {
            assertEquals("mv: Could not write to output stream", e.getMessage());
        }
    }

    // Test mv app with missing source file
    @Test
    void testMissingSourceFile() {
        // mv
        try {
            String[] args = {};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            assertEquals("mv: Missing file operand", e.getMessage());
        }
    }

    // Test mv app with invalid source file
    @Test
    void testInvalidSourceFile() {
        // mv invalidFile.txt tempFileB.txt
        try {
            String[] args = {"invalidFile.txt", getFileName(tempFileB)};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            assertEquals("mv: Cannot find 'invalidFile.txt'. No such file or directory.", e.getMessage());
        }
    }

    // Test mv app with missing destination file
    @Test
    void testMissingDestinationFile() {
        // mv tempFileA.txt
        String src = getFileName(tempFileA);
        try {
            String[] args = {src};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            assertEquals("mv: Missing destination file operand after '" + src + "'.", e.getMessage());
        }
    }

    // Test mv app to rename same file
    @Test
    void testRenamingSameFile() {
        // mv tempFileA.txt tempFileA.txt
        String src = getFileName(tempFileA);
        try {
            String[] args = {src, src};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            assertEquals("mv: '" + src + "' and '" + src + "' are the same file.", e.getMessage());//NOPMD
        }
    }

    // Test mv app rename file that will override existing file
    @Test
    void testRenameFileToExistingFileOverride() {
        // mv tempFileA.txt tempFileB.txt
        try {
            String[] args = {getFileName(tempFileA), getFileName(tempFileB)};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app rename file that will not override existing file
    @Test
    void testRenameFileToExistingFileExcludeOverride() {
        // mv -n tempFileA.txt tempFileB.txt
        String dest = getFileName(tempFileB);
        try {
            String[] args = {NO_OVERRIDE_FLAG, getFileName(tempFileA), dest};

            OutputStream outputStream = new ByteArrayOutputStream();
            mvApplication.run(args, inputStream, outputStream);
            assertEquals("mv: Destination file '" + dest + "' already exists and cannot be replaced.", outputStream.toString());
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app to move a file into a folder and override any existing file in it
    @Test
    void testMoveFileIntoExistingDirectoryOverride() {
        // mv tempFileA.txt tempDirA
        try {
            String[] args = {getFileName(tempFileA), getFileName(tempDirA)};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app to move file into a folder and not override any existing file in it
    @Test
    void testMoveFileIntoExistingDirectoryExcludeOverride() {
        // mv tempFileA.txt tempDirA
        try {
            String[] args = {NO_OVERRIDE_FLAG, getFileName(tempFileA), getFileName(tempDirA)};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app to move a file into a folder that already has a file with the same name and not override it
    @Test
    void testMoveFileIntoExistingDirectoryWithSameFileNameExcludeOverride() throws IOException {
        // mv tempFileA.txt tempDirA
        // Create a copy of a file in the directory, and we attempt to move the file outside this directory inside
        String src = getFileName(tempFileA);
        String directory = getFileName(tempDirA);
        Files.copy(tempFileA, tempDirA.resolve(src)); // create a duplicate of tempFileA.txt in dir
        try {
            String[] args = {NO_OVERRIDE_FLAG, src, directory};
            mvApplication.run(args, inputStream, outputStream);
            assertEquals("mv: A file with the same name already exists in "
                    + "directory '" + directory + "' and cannot be replaced.", outputStream.toString());
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app to move a file into existing folder with same file and override it
    @Test
    void testMoveFileIntoExistingDirectoryWithSameFileNameOverride() throws IOException {
        // mv tempFileA.txt tempDirA
        // Create a copy of a file in the directory, and we attempt to move the file outside this directory inside
        String src = getFileName(tempFileA);
        String directory = getFileName(tempDirA);
        Files.copy(tempFileA, tempDirA.resolve(src)); // create a duplicate of tempFileA.txt in dir
        try {
            String[] args = {src, directory};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            fail();
        }
    }


    // Test mv app move two files into a folder
    @Test
    void testMoveFilesIntoExistingDirectoryOverride() {
        // mv tempFileA.txt tempFileB.txt tempDirA
        try {
            String[] args = {getFileName(tempFileA), getFileName(tempFileB), getFileName(tempDirA)};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app move two files into a non existent folder
    @Test
    void testMoveFilesIntoNonExistentDirectoryOverride() {
        // mv tempFileA.txt tempFileB.txt invalidDirectory
        try {
            String[] args = {getFileName(tempFileA), getFileName(tempFileB), "nonExistFolder"};
            OutputStream outputStream = new ByteArrayOutputStream();
            mvApplication.run(args, inputStream, outputStream);
            assertEquals(ErrorConstants.ERR_MISSING_ARG, outputStream.toString());
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app to move two files into a file that is not a folder
    @Test
    void testMoveFilesIntoInvalidDirectoryOverride() {
        // mv tempFileA.txt tempFileB.txt tempFileA.txt
        String srcA = getFileName(tempFileA);
        try {
            String[] args = {srcA, getFileName(tempFileB), srcA};
            OutputStream outputStream = new ByteArrayOutputStream();
            mvApplication.run(args, inputStream, outputStream);
            assertEquals(ErrorConstants.ERR_MISSING_ARG, outputStream.toString());
        } catch (MvException e) {
            assertEquals("mv: '" + srcA + "' is not a directory.", e.getMessage());
        }
    }

    // Test mv app to move a folder into itself
    @Test
    void testMoveSameFolder() {
        // mv tempDirA tempDirA
        String src = getFileName(tempDirA);
        try {
            String[] args = {src, src};
            OutputStream byteStream = new ByteArrayOutputStream();
            mvApplication.run(args, inputStream, byteStream);
            assertFalse(byteStream.toString().isEmpty());
        } catch (MvException e) {
            fail();
        }
    }

    /*
     * Test mv app move a folder into itself, and move a txt file into said folder
     * The txt file will be successfully moved into the folder
     */
    @Test
    void testMoveSameFolderWithAnotherValidFile() {
        // mv tempDirA tempFileB.txt tempDirA
        String src = getFileName(tempDirA);
        try {
            String[] args = {src, getFileName(tempFileB), src};
            mvApplication.run(args, inputStream, outputStream);
            assertEquals("mv: "+ src + " is the sub dir of " + src + " or they are the same file.",outputStream.toString());
        } catch (MvException e) {
            fail();
        }
    }

    // Test mv app to move a folder into another folder, both are different folder
    @Test
    void testMoveDifferentFolder() {
        // mv tempDirA tempDirB
        try {
            String[] args = {getFileName(tempDirA), getFileName(tempDirB)};
            mvApplication.run(args, inputStream, outputStream);
        } catch (MvException e) {
            fail();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Helper for MvApplication">
    private String getFileName(Path path) {
        return path.getFileName().toString();
    }

    private void delete(Path path, boolean isFolder) throws IOException {
        if (isFolder) {
            // Extracted from https://stackoverflow.com/questions/35988192/java-nio-most-concise-recursive-directory-delete
            if (path.toFile().exists()) {
                try (Stream<Path> walk = Files.walk(path)) {
                    walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            //.peek(p -> System.out.println("Deleting " + p.getPath()))
                            .forEach(File::delete);
                }
            }
        } else {
            path.toFile().delete();
        }
    }
    //</editor-fold>
}
