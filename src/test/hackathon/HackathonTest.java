package hackathon;

import org.junit.jupiter.api.*;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.app.RmApplication;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class HackathonTest {
    private static final String ORIGINAL_DIR = Environment.currentDirectory;
    private static final Path TEST_PATH =
            IOUtils.resolveFilePath("hackFiles" + StringUtils.fileSeparator() + "test3");

    private Path unreadableFilePath = null;
    private Path unreadableFolderPath = null;
    private Path tempFile1Path = null;
    private Path tempFile2Path = null;
    private Path tempFolder1Path = null;

    private Shell shell = new ShellImpl();
    private final String directory = "hackFiles" + StringUtils.fileSeparator();
    private OutputStream outputStream;

    @BeforeEach
    void setUp() {
        shell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
        try {
            unreadableFilePath = Files.createTempFile(TEST_PATH, "unreadable", "txt");
            File f = new File(unreadableFilePath.toString());
            assertTrue(f.exists());
            f.setReadable(false);

            unreadableFolderPath = Files.createTempDirectory(TEST_PATH, "unreadable");
            f = new File(unreadableFolderPath.toString());
            assertTrue(f.exists());
            f.setReadable(false);

            tempFile1Path = Files.createTempFile(TEST_PATH, "file1", "txt");
            f = new File(tempFile1Path.toString());
            assertTrue(f.exists());

            tempFile2Path = Files.createTempFile(TEST_PATH, "file2", "txt");
            f = new File(tempFile2Path.toString());
            assertTrue(f.exists());

            tempFolder1Path = Files.createTempDirectory(TEST_PATH, "folder1");
            f = new File(tempFolder1Path.toString());
            assertTrue(f.exists());
        } catch (Exception e) {
            fail("Failed to initialise");
        }
    }

    @AfterEach
    void tearDown() {
        Environment.currentDirectory = ORIGINAL_DIR;
        try {
            if (unreadableFilePath != null) {
                File f = new File(unreadableFilePath.toString());
                f.setReadable(true);
                Files.deleteIfExists(unreadableFilePath);
            }
            if (unreadableFolderPath != null) {
                File f = new File(unreadableFolderPath.toString());
                f.setReadable(true);
                Files.deleteIfExists(unreadableFolderPath);
            }

            Files.deleteIfExists(tempFile1Path);
            Files.deleteIfExists(tempFile2Path);
            Files.deleteIfExists(tempFolder1Path);
        } catch (Exception e) {
            fail("Failed to tear down");
        }
        assertDoesNotThrow(() -> outputStream.close());
    }


    /**
     * cd without arguments should change directory to home
     */
    @Test
    void cdWithoutArguments() {
        assertDoesNotThrow(() -> {
            shell.parseAndEvaluate("cd", outputStream);
        });
    }


    /**
     * Cut: pasing in stdin argument should throw OutOfRange
     *
     * @throws AbstractApplicationException
     * @throws ShellException
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void cutStdinArgumentOnlyShouldThrowOutOfRange() throws IOException, AbstractApplicationException, ShellException {
        // echo baz | cut -c -
        String cmdStr = "echo baz | cut -c -";
        String file1 = directory + "CutTestFolder" + StringUtils.fileSeparator() + "file1.txt";
        String file2 = directory + "CutTestFolder" + StringUtils.fileSeparator() + "file2.txt";
        Exception expected = assertThrows(CutException.class, () -> shell.parseAndEvaluate(cmdStr, outputStream),
                "should throw cut exception");
        assertEquals(ERR_OUT_RANGE, expected.getMessage());
    }

    /**
     * Command Substitution: prints extra space per resolved argument
     */
    @Test
    void nestedQuotingTest() {
        // echo "'This is space `echo " "`'"
        // Output: 'This is space  ' (2 spaces)
        final String cmdStr1 = "echo \"'This is space `echo \" \"`'\"";
        assertDoesNotThrow(() -> shell.parseAndEvaluate(cmdStr1, outputStream),
                "valid command string should not throw exception");
        assertEquals("'This is space  '" + StringUtils.STRING_NEWLINE, outputStream.toString()); // Actual: 3 spaces

        // echo "'This is space `echo " "`'"
        // Output: 'This is space  ' (4 spaces)
        outputStream = new ByteArrayOutputStream();
        final String cmdStr2 = "echo \"'This is space `echo \" \"` `echo \" \"`'\"";
        assertDoesNotThrow(() -> shell.parseAndEvaluate(cmdStr2, outputStream),
                "valid command string should not throw exception");
        assertEquals("'This is space    '" + StringUtils.STRING_NEWLINE, outputStream.toString()); // Actual: 6spaces
    }

    /**
     * Globbing tests: ls folder/*
     * Generally all globbing with asterisk(*) at the end of argument (path/to/folder/*) does not resolve properly
     *
     * @throws AbstractApplicationException
     * @throws ShellException
     */
    @Test
    void lsWithFolderGlobbing() throws AbstractApplicationException, ShellException {
        String cmdStr = "ls hackFiles" + StringUtils.fileSeparator() + "GlobbingTestFolder" + StringUtils.fileSeparator() + "*.txt";
        assertDoesNotThrow(() -> shell.parseAndEvaluate(cmdStr, outputStream),
                "valid command string should not throw exception");
    }

    /**
     * Paste: incorrect output for multiple files and output streams.
     * The example command tested here is testing "paste test1.txt - test2.txt -"
     * where the stdin is contents of test1.txt. By right the first line of output
     * should contain 3 "tree"s (first line of test1.txt), but the first line of
     * output contains 2 "tree"s and a "damaging" (second line of test1.txt).
     * (and many other differences)
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void pasteMultipleFilesAndStdin() throws AbstractApplicationException, ShellException, IOException {
        String cmdStr = "paste hackFiles/pasteTest/test1.txt | paste hackFiles/pasteTest/test1.txt - hackFiles/pasteTest/test2.txt -";
        byte[] expectedBytes = Files.readAllBytes(Paths.get(directory + "pasteTest" + StringUtils.fileSeparator() + "resultFileAndMultipleStdin.txt"));
        shell.parseAndEvaluate(cmdStr, outputStream);
        assertEquals(new String(expectedBytes, StandardCharsets.UTF_8), outputStream.toString());
    }

    /**
     * cp: file not found exception not thrown for non-existant source file
     * In this test case, it returns an output of the file name, but
     * no exceptions were thrown, and no indication to users that cp was not successful.
     * After running the command, the destination file is incorrectly deleted too.
     * Because the test1.txt will be deleted after running this test, please recreate
     * test1.txt using the test1_backup.txt
     *
     * @throws AbstractApplicationException
     * @throws ShellException
     */
    @Test
    @Disabled
    @DisplayName("we assume output exception message to stdout instead of throw to main method")
    void cpNonExistentSource() throws AbstractApplicationException, ShellException {
        String cmdStr = "cp hackFiles/cpTest/hweoirw.txt hackFiles/cpTest/test1.txt";
        shell.parseAndEvaluate(cmdStr, outputStream);
        String thrown = assertThrows(CpException.class, () -> shell.parseAndEvaluate(cmdStr, outputStream)).getMessage();
        assertEquals("cp: " + ERR_FILE_NOT_FOUND, thrown);
    }

    /**
     * cp: exception not thrown for copying directories into files.
     * Instead of throwing exception, the destination file is changed into a directory
     * This behaviour was not documented in the assumptions and does not follow normal
     * UNIX shell behaviour.
     * Because the test1.txt will be deleted after running this test, please recreate
     * test1.txt using the test1_backup.txt
     *
     * @throws AbstractApplicationException
     * @throws ShellException               BugFix: we have fixed this bug, we will throw exception if target has exited.
     */
    @Test
    @Disabled
    void cpSourceIsDirectory() throws AbstractApplicationException, ShellException {
        String cmdStr = "cp hackFiles/cpTest/folder hackFiles/cpTest/test1.txt";
        shell.parseAndEvaluate(cmdStr, outputStream);
        String thrown = assertThrows(CpException.class, () -> shell.parseAndEvaluate(cmdStr, outputStream)).getMessage();
        assertEquals("cp: " + ERR_IS_DIR, thrown);
    }

    /**
     * cp: exception not thrown for copying files into a non-existent directory.
     * Instead of throwing exception or creating a new directory of that name,
     * it creates a new file of that name and copies into that.
     */
    @Test
    @Disabled
    @DisplayName("we assume output exception message to stdout instead of throw to main method")
    void cpIntoNonExistentDirectory() {
        String cmdStr = "cp hackFiles/cpTest/test1.txt hackFiles/cpTest/nonexistent/";
        assertThrows(CpException.class, () -> shell.parseAndEvaluate(cmdStr, outputStream));

    }

    /**
     * diff: format of stating binary files are different (-q flag) does not follow the format
     * in the project description pdf example: "Binary files A/image2.bmp and B/image2.bmp
     * differ". Instead, their output doesn't say "Binary" at all.
     * Same for comparing binary files of the same name in two directories.
     */
    @Test
    void diffComparingBinaryFilesOutputFormat() throws AbstractApplicationException, ShellException {
        String cmdStr = "diff -q hackFiles/diffTest/image1.png hackFiles/diffTest/image2.png";
        String expected = "Binary files hackFiles/diffTest/image1.png and hackFiles/diffTest/image2.png differ";
        shell.parseAndEvaluate(cmdStr, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    /**
     * diff: doesn't adhere to specifications on how to handle non-text files.
     * According to Q&A, for non-text files show only if they differ or not, no matter the flag.
     * This group's implementation outputs lines of gibberish instead.
     * Same for comparing binary files of the same name in two directories.
     */
    @Test
    void diffComparingBinaryFiles() throws AbstractApplicationException, ShellException {
        String cmdStr = "diff hackFiles/diffTest/image1.png hackFiles/diffTest/image2.png";
        String expected = "Binary files hackFiles/diffTest/image1.png and hackFiles/diffTest/image2.png differ";
        shell.parseAndEvaluate(cmdStr, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    /**
     * diff: comparing two directories with directories inside them incorrectly
     * throws no such file/directory exception. Also doesn't indicate which file/
     * directory was not found.
     */
    @Test
    void diffComparingDirectoriesWithDirectoriesInside() {
        String cmdStr = "diff hackFiles/diffTest/folder1 hackFiles/diffTest/folder2";
        assertDoesNotThrow(() -> shell.parseAndEvaluate(cmdStr, outputStream));
    }

    /**
     * diff: order of the files is not preserved.
     * In this example, test1.txt is taken in as stdin. So diff - test2.txt
     * and diff test2.txt - should have different outputs
     *
     * Adapted according to our assumption: While diff between stdin with file,
     * no matter what arg order is given, the order is always diff file -
     */
    @Test
    void diffFileWithStdin() throws AbstractApplicationException, ShellException {
        String cmdStr1 = "paste hackFiles/diffTest/test1.txt | diff - hackFiles/diffTest/test2.txt";
        String cmdStr2 = "paste hackFiles/diffTest/test1.txt | diff hackFiles/diffTest/test2.txt -";
        shell.parseAndEvaluate(cmdStr1, outputStream);
        String result1 = outputStream.toString();
        outputStream = new ByteArrayOutputStream();
        shell.parseAndEvaluate(cmdStr2, outputStream);
        String result2 = outputStream.toString();
        assertEquals(result1, result2);
    }

    /**
     * Bug #10: ls does not return the correct error when used on folders with no permission.
     * Also breaks -R and causes shell to terminate with NullPointerException.
     *
     * @throws Exception
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void lsNoPermissions() throws Exception {
        Environment.currentDirectory = TEST_PATH.toString();
        try {
            shell.parseAndEvaluate(String.format("ls %s", unreadableFolderPath), outputStream);
            assertTrue(outputStream.toString().contains(ERR_NO_PERM));
        } catch (LsException le) {
            assertTrue(le.getMessage().contains(ERR_NO_PERM));
        }
    }

    /**
     * Bug 11: ls fails to provide correct output when used recursively on arguments
     * <a> and <b> where <b> is a folder in <a>. May seem incorrectly formatted but
     * may also be difficult to distinguish.
     */
    @Test
    void lsFolderWithinFolder() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = "ls -R ls ls" + CHAR_FILE_SEP + "ls2";
        assertDoesNotThrow(() -> shell.parseAndEvaluate(command, outputStream));
        String expected =
                "ls:" + STRING_NEWLINE +
                        "1.txt" + STRING_NEWLINE +
                        "ls2" + STRING_NEWLINE + STRING_NEWLINE +
                        "ls" + CHAR_FILE_SEP + "ls2:" + STRING_NEWLINE +
                        "1.txt" + STRING_NEWLINE + STRING_NEWLINE +
                        "ls" + CHAR_FILE_SEP + "ls2:" + STRING_NEWLINE +
                        "1.txt";
        assertEquals(expected, outputStream.toString().trim());
    }

    /**
     * Bug 12: find fails to execute on valid folder paths when folder with no read permission is provided.
     * Should output permission denied errors for relevant folders and proper output for valid folders.
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void findMixedPermissions() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = String.format("find %s %s -name '1.txt'",
                unreadableFolderPath, "ls");
        Throwable thrown = assertThrows(FindException.class,
                () -> shell.parseAndEvaluate(command, outputStream));
        String output = outputStream.toString().trim();
        assertTrue(output.contains("ls" + StringUtils.fileSeparator() + "1.txt"));
        assertTrue(output.contains("ls" + StringUtils.fileSeparator() + "ls2" + StringUtils.fileSeparator() + "1.txt"));

        String error = thrown.getMessage();
        assertTrue(error.contains("find"));
        assertTrue(error.contains(ERR_NO_PERM)); // Satisfies
    }

    /**
     * Bug 13: find incorrectly throws a file not found error and fails to deliver correct output when used
     * on a valid file with regex argument containing the valid file’s name. Should output the valid file’s
     * name.
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void findValidFile() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = String.format("find ls" + StringUtils.fileSeparator() + "1.txt -name '1.txt'");
        assertDoesNotThrow(() -> shell.parseAndEvaluate(command, outputStream));
        String output = outputStream.toString().trim();
        String expected = "ls" + StringUtils.fileSeparator() + "1.txt";
        assertEquals(expected, output);
    }

    /**
     * Bug 14: sort fails to return proper error for invalid flag - error claims
     * no such file or directory.
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void sortInvalidArgs() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = "sort -nerf sort" + StringUtils.fileSeparator() + "numerical.txt";
        try {
            shell.parseAndEvaluate(command, outputStream);
            String output = outputStream.toString();
            assertTrue(output.contains("sort"));
            assertTrue(output.contains(ERR_INVALID_FLAG));
            assertTrue(output.contains("e"));
        } catch (Exception e) {
            String error = e.getMessage();
            assertTrue(error.contains("sort"));
            assertTrue(error.contains(ERR_INVALID_FLAG));
            assertTrue(error.contains("e"));
        }
    }

    /**
     * Bug 15: sort fails to properly sort numerically (-n). Linux/macOS shell compares
     * using numerical chunks (from left to right) on each line and follow this ordering
     * (from smallest to largest):
     * <p>
     * 1. Negative real numbers until -1
     * 2. Non alphabetic characters (not numerical)
     * 3. 0
     * 4. Capitalised alphabetic characters (not numerical)
     * 5. Non-capitalised alphabetic characters (not numerical)
     * 6. Real numbers from 1 onwards
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void sortNumerical() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = "sort -n sort" + StringUtils.fileSeparator() + "numerical.txt";
        assertDoesNotThrow(() -> shell.parseAndEvaluate(command, outputStream));
        try {
            String expected = new String(Files.readAllBytes(IOUtils.resolveFilePath(
                    "sort" + StringUtils.fileSeparator() + "numericalResult.txt"
            )));
            assertEquals(expected, outputStream.toString().trim());
        } catch (IOException e) {
            fail("Failed to read result: " + e.getMessage());
        }
    }

    /**
     * Bug 16: mv fails to return proper error for invalid file argument - error
     * only prints a file path. Error should inform that file is not found.
     */
    @Test
    void mvInvalidFile() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = "mv -n invalidFile invalidFile2";
        try {
            shell.parseAndEvaluate(command, outputStream);
            File iFile = IOUtils.resolveFilePath("invalidFile").toFile();
            File iFile2 = IOUtils.resolveFilePath("invalidFile2").toFile();
            assertFalse(iFile.exists());
            assertFalse(iFile2.exists());
            String output = outputStream.toString().trim();
            assertTrue(output.contains("mv"));
            assertTrue(output.contains(ERR_FILE_NOT_FOUND));
            assertFalse(iFile.exists());
            assertFalse(iFile2.exists());
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("mv"));
            assertTrue(e.getMessage().contains(ERR_FILE_NOT_FOUND));
        }
    }

    /**
     * Bug 17: mv fails to return proper error for moving valid folder into a valid file <a>
     * - error states file already exists. Error should inform that <a> is not a directory.
     */
    @Test
    @Disabled
    @DisplayName("we assume mv file to a file will replace this file by default")
    void mvValidFolderIntoValidFile() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = String.format("mv -n %s %s", tempFolder1Path, tempFile1Path);
        try {
            shell.parseAndEvaluate(command, outputStream);
            String output = outputStream.toString().trim();
            assertTrue(tempFolder1Path.toFile().exists());
            assertTrue(tempFile1Path.toFile().exists());
            assertTrue(output.contains("mv"));
            assertTrue(output.contains(ERR_IS_NOT_DIR));
        } catch (Exception e) {
            String error = e.getMessage();
            assertTrue(tempFolder1Path.toFile().exists());
            assertTrue(tempFile1Path.toFile().exists());
            assertTrue(error.contains("mv"));
            assertTrue(error.contains(ERR_IS_NOT_DIR));
        }
    }

    /**
     * Bug 18: mv fails to replace valid file <a> over valid file <b> with -n flag (according
     * to assumptions). Does not replace without -n flag either. Should replace with no error.
     * <p>
     * Fix: the assumption we made is wrong,
     * By default, it will overwrite an existing file.
     * With the "-n" flag, it will not overwrite any existing file.
     * If remove the flag "-n" this test will past.
     */
    @Test
    @Disabled
    void mvReplaceValidFile() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = String.format("mv -n %s %s", tempFile1Path, tempFile2Path);
        assertDoesNotThrow(() -> shell.parseAndEvaluate(command, outputStream));
        File tempFile1 = tempFile1Path.toFile();
        assertFalse(tempFile1.exists());
        File tempFile2 = tempFile2Path.toFile();
        assertTrue(tempFile2.exists());
        String output = outputStream.toString().trim();
        String expected = "";
        assertEquals(expected, output);
    }

    /**
     * Bug 19: mv fails to execute on valid file and folder paths arguments when file/folder paths
     * arguments with no read permission are provided. Also fails to throw proper errors - error
     * only states a path. Should output permission denied errors for relevant files/folders and
     * execute otherwise.
     */
    @Test
    @Disabled
    @DisplayName("ignore permission related")
    void mvReplaceMixedFiles() {
        Environment.currentDirectory = TEST_PATH.toString();
        String command = String.format("mv -n %s %s %s",
                "invalidFile.txt", tempFile1Path, tempFolder1Path);
        try {
            shell.parseAndEvaluate(command, outputStream);
            String output = outputStream.toString().trim();
            File tempFile1 = tempFile1Path.toFile();
            assertFalse(tempFile1.exists());
            tempFile1 = IOUtils.resolveFilePath(
                    String.format("%s" + StringUtils.fileSeparator() + "%s",
                            tempFolder1Path.toString(), tempFile1Path.getFileName())).toFile();
            assertTrue(tempFile1.exists());
            assertTrue(output.contains("mv"));
            assertTrue(output.contains(ERR_FILE_NOT_FOUND));
        } catch (Exception e) {
            String error = e.getMessage();
            assertTrue(error.contains("mv"));
            assertTrue(error.contains(ERR_FILE_NOT_FOUND));
            File tempFile1 = tempFile1Path.toFile();
            assertFalse(tempFile1.exists());
            tempFile1 = IOUtils.resolveFilePath(
                    String.format("%s" + StringUtils.fileSeparator() + "%s",
                            tempFolder1Path.toString(), tempFile1Path.getFileName())).toFile();
            assertTrue(tempFile1.exists());
        }
    }


    /**
     * RmApplication test #1: Rm multiple files, first of which do not exist.
     * This would have a different result if the valid files are placed before the invalid files.
     * This behavior is not mentioned under Assumptions.
     */
    @Test
    @Disabled
    @DisplayName("assumption added")
    void rmMultipleFilesSomeNotExist() throws Exception {
        RmApplication rmApplication = new RmApplication();
        String testFileName1 = System.getProperty("user.dir") + "/testFile1";
        String testFileName3 = System.getProperty("user.dir") + "/testFile3";

        File testFile1 = new File(testFileName1);
        File testFile3 = new File(testFileName3);

        testFile1.createNewFile();
        testFile3.createNewFile();

        rmApplication.run(new String[]{"testFile2", testFileName1, testFileName3}, System.in, System.out);
        assertFalse(testFile1.exists());
        assertFalse(testFile3.exists());
    }

    /**
     * RmApplication test #2: Rm file inside folder without execute permission.
     * Assumptions do not mention anything about deleting files without execute permission.
     */
    @Test
    @DisplayName("permission related")
    @Disabled
    void rmFileInFolderWithNoExecutePermission() throws Exception {
        RmApplication rmApplication = new RmApplication();
        String testDirectoryName = System.getProperty("user.dir") + "/testDir2";
        File testDirectory = new File(testDirectoryName);
        testDirectory.mkdir();

        String testFileName = testDirectoryName + "/testFile2";
        File testFile = new File(testFileName);
        testFile.createNewFile();

        testDirectory.setExecutable(false);
        rmApplication.run(new String[]{"testDir2/testFile2"}, System.in, System.out);
        testDirectory.setExecutable(true);
        assertFalse(testFile.exists());
    }

    /**
     * RmApplication test #3: Rm file inside folder without write permission.
     */
    @Test
    @Disabled
    void rmFileInsideFolderWithNoWritePermission() throws Exception {
        RmApplication rmApplication = new RmApplication();
        String testDirectoryName = System.getProperty("user.dir") + "/testDir1";
        File testDirectory = new File(testDirectoryName);
        testDirectory.mkdir();

        String testFileName = testDirectoryName + "/testFile1";
        File testFile = new File(testFileName);
        testFile.createNewFile();

        testDirectory.setWritable(false);
        rmApplication.run(new String[]{"testDir1/testFile1"}, System.in, System.out);
        testDirectory.setWritable(true);
        assertFalse(testFile.exists());
    }

    /**
     * cut: does not handle range with lower bound eg. 2- or -5
     * 2-: substring with index 2 to the end of the string or
     * -5: substring from start to index 2
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void oneIndexRangeArgument() throws AbstractApplicationException, ShellException {
        final String cmd = "echo baz | cut -c 2-";
        assertDoesNotThrow(() -> {
            shell.parseAndEvaluate(cmd, outputStream);
        });
        assertEquals("az", outputStream.toString());

        outputStream = new ByteArrayOutputStream();
        final String cmd2 = "echo baz | cut -c -2";
        assertDoesNotThrow(() -> {
            shell.parseAndEvaluate(cmd2, outputStream);
        });
        assertEquals("ba", outputStream.toString());
    }

    /**
     * cut: does not handle invalid list of integers: one integer before , eg. 2, or ,2
     * Expected: Should throw invalid range exception/invalid arg exception
     */
    @Test
    @Disabled
    @DisplayName("Invalid")
    void oneIndexListArgument() throws AbstractApplicationException, ShellException {
        final String cmd = "echo baz | cut -c 2,";
        Exception exception = assertThrows(CutException.class, () -> {
            shell.parseAndEvaluate(cmd, outputStream);
        });
        assertEquals(ERR_INVALID_RANGE, exception.getMessage());


        final String cmd2 = "echo baz | cut -c ,2";
        exception = assertThrows(CutException.class, () -> {
            shell.parseAndEvaluate(cmd2, outputStream);
        });
        assertEquals(ERR_INVALID_RANGE, exception.getMessage());
    }
}