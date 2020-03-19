package tdd.ef2;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.impl.app.FindApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class FindApplicationTest {
    String srcDir = "src"+CHAR_FILE_SEP+"test"+CHAR_FILE_SEP+"tdd"+CHAR_FILE_SEP+"util"+CHAR_FILE_SEP+
            "dummyTestFolder"+CHAR_FILE_SEP+"FindTestFolder";
    String testDir = "src"+CHAR_FILE_SEP+"test"+CHAR_FILE_SEP+"tdd"+CHAR_FILE_SEP+"util"+CHAR_FILE_SEP+
            "dummyTestFolder"+CHAR_FILE_SEP+"FindTestFolder"+CHAR_FILE_SEP+"sampleFiles/";
    String testDir2 = "src"+CHAR_FILE_SEP+"test"+CHAR_FILE_SEP+"tdd"+CHAR_FILE_SEP+"util"+CHAR_FILE_SEP+
            "dummyTestFolder"+CHAR_FILE_SEP+"FindTestFolder"+CHAR_FILE_SEP+"sampleFiles2";
    String testDir3 = "src"+CHAR_FILE_SEP+"test"+CHAR_FILE_SEP+"tdd"+CHAR_FILE_SEP+"util"+CHAR_FILE_SEP+
            "dummyTestFolder"+CHAR_FILE_SEP+"FindTestFolder"+CHAR_FILE_SEP+"sampleFiles3";
    String sample1 = "sampleNotExist";
    String sample2 = "sampleNotExist2";
    static FindApplication app;
    InputStream in;
    OutputStream out;

    @BeforeAll
    static void setUp() {
        app = new FindApplication();
    }

    @AfterAll
    static void tearDown() {
    }

    @BeforeEach
    void init() {
        in = new ByteArrayInputStream("".getBytes());
        out = new ByteArrayOutputStream();
    }

    @AfterEach
    void done() {
    }

    // ===========================================
    // Test cases for finding folder content
    // ===========================================

    @Test
    // MUT: findFolderContent
    // Test Case: FileName Null, 0 Folders Specified (Null)
    void FindFolderContent_FileNameNullNullFolderSpecified_ThrowException(){
        assertThrows(FindException.class, () -> app.findFolderContent(null, null));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: FileName Null, 1 Folder
    void FindFolderContent_FileNameNull1Folder_ThrowException(){
        assertThrows(FindException.class, () -> app.findFolderContent(null, testDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: FileName Null, >1 Folders
    void FindFolderContent_FileNameNullMultipleFolder_ThrowException(){
        assertThrows(FindException.class, () -> app.findFolderContent(null, testDir, testDir2));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One Invalid FileName Specified, 1 Valid Folder
    void FindFolderContent_1InvalidFileName1ValidFolder_OutputToStream() throws Exception {
        assertEquals("", app.findFolderContent("testInvalidFileName", "src"));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Valid Folder
    void FindFolderContent_1FileName1ValidFolder_OutputToStream() throws Exception {
        String expected = testDir2 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + "c.txt";
        assertEquals(expected, app.findFolderContent("c.txt", srcDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified (but not found), 1 Valid Folder
    void FindFolderContent_1FileNameFileNotFound1ValidFolder_OutputToStream() throws Exception {
        assertEquals("", app.findFolderContent("a.txt", testDir2));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Invalid Folder i.e. Folder does not exist
    void FindFolderContent_1FileName1InvalidFolderFolderDNE_OutputToStream() throws Exception {
        String expected = "find: " + sample1 + ": No such file or directory";
        assertEquals(expected, app.findFolderContent("c.txt", "sampleNotExist"));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Invalid Folder i.e. File not in folder specified
    void FindFolderContent_1FileName1InvalidFolderFileNotFound_OutputToStream() throws Exception {
        assertEquals("", app.findFolderContent("c.txt", testDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, >1 Valid Folders i.e. File found in multiple folders
    // Note: didn't account for case if file found in multiple folders
    void FindFolderContent_1FileNameMultipleValidFolders_OutputToStream() throws Exception {
        String expected = testDir2 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + "c.txt";
        assertEquals(expected, app.findFolderContent("c.txt", srcDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Valid Folder 1 Invalid Folder i.e. Folder does not exist
    void FindFolderContent_1FileName1Valid1InvalidDNEFolder_OutputToStream() throws Exception {
        String expected = testDir2 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE +
                "find: " + sample1 + ": No such file or directory";
        assertEquals(expected, app.findFolderContent("c.txt", srcDir, sample1));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Valid Folder 1 Invalid folder i.e. file not in folder specified
    void FindFolderContent_1FileName1Valid1InvalidFolderFileNotFound_OutputToStream() throws Exception {
        String expected = testDir2 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + "c.txt";
        assertEquals(expected, app.findFolderContent("c.txt", srcDir, testDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, >1 Folders All Invalid i.e. Folders do not exist
    void FindFolderContent_1FilenameMultipleInvalidFoldersDNE_OutputToStream() throws Exception {
        String expected = "find: " + sample1 + ": No such file or directory" + System.lineSeparator() +
                "find: " + sample2 + ": No such file or directory";
        assertEquals(expected, app.findFolderContent("c.txt", "sampleNotExist", "sampleNotExist2"));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, >1 Folders All Invalid i.e. File not in folders specified
    void FindFolderContent_1FileNameMultipleInvalidFoldersFileNotFound_OutputToStream() throws Exception {
        assertEquals("", app.findFolderContent("a.txt", testDir2, testDir3));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 0 Folders Specified
    void FindFolderContent_1FileNameNoFoldersSpecified_OutputToStream(){
        assertThrows(FindException.class, () -> app.findFolderContent("a.txt", null));
    }

    // ===========================================
    // Test cases for find command parsing and running
    // ===========================================

    @Test
    // MUT: run
    // Test Case: stdout == null
    void Run_StdOutNull_ThrowException(){
        String[] args = {"src", "-name", "c.txt"};
        assertThrows(FindException.class, () -> app.run(args, in, null));
    }

    @Test
    void Run_3ArgsPositive_OutputToStream() throws AbstractApplicationException {
        String[] args = {srcDir, "-name", "c.txt"};
        String expected = testDir2 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE;
        app.run(args, in, out);
        assertEquals(expected, out.toString());
    }

    @Test
    // Test Case: 4 Args Multiple FileName; FOLDERS -name FILENAME FILENAME
    void Run_4Args2Filename_ThrowException(){
        String[] args = {"src", "-name", "c.txt", "d.txt"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 4 Args Multiple Folder; FOLDER FOLDER -name FILENAME
    void Run_4ArgsPositiveMultipleFolders_OutputToStream() throws AbstractApplicationException {
        String[] args = {testDir2, testDir3, "-name", "c.txt"};
        app.run(args, in, out);
        String expected = testDir2 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + "c.txt" + STRING_NEWLINE;
        assertEquals(expected, out.toString());
    }

    @Test
    // Test Case: 4 Args Multiple Folder Incorrect Order; -name FILENAME FOLDER FOLDER
    void Run_4ArgsMultipleFoldersIncorrectOrder_ThrowException() {
        String[] args = {"-name", "c.txt", "src", "lib"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 3 Args Incorrect Order 1; -name FILENAME FOLDER
    void Run_3ArgsIncorrectOrder1_ThrowException() {
        String[] args = {"-name", "c.txt", "src"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 3 Args Incorrect Order 2; FOLDER FILENAME -name
    void Run_3ArgsIncorrectOrder2_ThrowException() {
        String[] args = {"src", "c.txt", "-name"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 3 Args Incorrect Order 3; -name FOLDER FILENAME
    void Run_3ArgsIncorrectOrder3_ThrowException() {
        String[] args = {"-name", "src", "c.txt"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 3 Args Incorrect Order 4; FILENAME -name FOLDER
    void Run_3ArgsIncorrectOrder4_OutputToStream() throws AbstractApplicationException {
        String[] args = {"c.txt", "-name", "src"};
        String expected = "find: c.txt: No such file or directory" + System.lineSeparator();
        app.run(args, in, out);
        assertEquals(expected, out.toString());
    }

    @Test
    // Test Case: 3 Args Incorrect Order 5; FILENAME FOLDER -name
    void Run_3ArgsIncorrectOrder5_ThrowException() {
        String[] args = {"c.txt", "src", "-name"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 2 Args; FOLDER FILENAME
    void Run_2ArgsNoFlag_ThrowException() {
        String[] args = {"src", "c.txt"};
        assertThrows(FindException.class, () -> app.run(args,in, out));
    }

    @Test
    // Test Case: 2 Args; FOLDER -name
    void Run_2ArgsNoFileName_ThrowException(){
        String[] args = {"src", "-name"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 2 Args; -name FILENAME
    void Run_2ArgsNoFolder_ThrowException() {
        String[] args = {"-name", "c.txt"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 1 Arg; FOLDER
    void Run_1ArgFolder_ThrowException() {
        String[] args = {"src"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 1 Arg; -name
    void Run_1ArgFlag_ThrowException() {
        String[] args = {"-name"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    // Test Case: 1 Arg; FILENAME
    void Run_1ArgFileName_ThrowException() {
        String[] args = {"-c.txt"};
        assertThrows(FindException.class, () -> app.run(args, in, out));
    }

    @Test
    void Run_EmptyArgs_OutputToStream() {
        String[] args = { };
        assertThrows(FindException.class, () -> app.run(args,in, out));
    }

    @Test
    void Run_NullArgs_OutputToStream() {
        assertThrows(FindException.class, () -> app.run(null, in, out));
    }

}
