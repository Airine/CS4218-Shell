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
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class FindApplicationTest {
    String srcDir = "src"+CHAR_FILE_SEP+"test"+CHAR_FILE_SEP+"tdd"+CHAR_FILE_SEP+"util"+CHAR_FILE_SEP+
            "dummyTestFolder"+CHAR_FILE_SEP+"FindTestFolder";
    String testDir = srcDir+CHAR_FILE_SEP+"sampleFiles";
    String testDir2 = srcDir+CHAR_FILE_SEP+"sampleFiles2";
    String testDir3 = srcDir+CHAR_FILE_SEP+"sampleFiles3";
    String sample1 = "sampleNotExist";
    String sample2 = "sampleNotExist2";
    String fileNameC = "c.txt";
    String findPrefix = "find: ";
    String suffix = "-name";
    static FindApplication app;
    InputStream inputStream;
    OutputStream outputStream;

    @BeforeAll
    static void setUp() {
        app = new FindApplication();
    }

    @BeforeEach
    void init() {
        inputStream = new ByteArrayInputStream("".getBytes());
        outputStream = new ByteArrayOutputStream();
    }

    // ===========================================
    // Test cases for finding folder content
    // ===========================================

    @Test
    // MUT: findFolderContent
    // Test Case: FileName Null, 0 Folders Specified (Null)
    void testFindFolderContentFileNameNullNullFolderSpecifiedThrowException(){
        assertThrows(FindException.class, () -> app.findFolderContent(null, null));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: FileName Null, 1 Folder
    void testFindFolderContentFileNameNull1FolderThrowException(){
        assertThrows(FindException.class, () -> app.findFolderContent(null, testDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: FileName Null, >1 Folders
    void testFindFolderContentFileNameNullMultipleFolderThrowException(){
        assertThrows(FindException.class, () -> app.findFolderContent(null, testDir, testDir2));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One Invalid FileName Specified, 1 Valid Folder
    void testFindFolderContent1InvalidFileName1ValidFolderOutputToStream() throws Exception {
        assertEquals("", app.findFolderContent("testInvalidFileName", srcDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Valid Folder
    void testFindFolderContent1FileName1ValidFolderOutputToStream() throws Exception {
        String expected = testDir2 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + fileNameC;
        assertEquals(expected, app.findFolderContent(fileNameC, srcDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified (but not found), 1 Valid Folder
    void testFindFolderContent1FileNameFileNotFound1ValidFolderOutputToStream() throws Exception {
        assertEquals("", app.findFolderContent("a.txt", testDir2));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Invalid Folder i.e. Folder does not exist
    void testFindFolderContent1FileName1InvalidFolderFolderDNEOutputToStream() throws Exception {
        String expected = findPrefix + sample1 + ": " + ERR_FILE_NOT_FOUND;
        assertEquals(expected, app.findFolderContent(fileNameC, "sampleNotExist"));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Invalid Folder i.e. File not in folder specified
    void testFindFolderContent1FileName1InvalidFolderFileNotFoundOutputToStream() throws Exception {
        assertEquals("", app.findFolderContent(fileNameC, testDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, >1 Valid Folders i.e. File found in multiple folders
    // Note: didn't account for case if file found in multiple folders
    void testFindFolderContent1FileNameMultipleValidFoldersOutputToStream() throws Exception {
        String expected = testDir2 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + fileNameC;
        assertEquals(expected, app.findFolderContent(fileNameC, srcDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Valid Folder 1 Invalid Folder i.e. Folder does not exist
    void testFindFolderContent1FileName1Valid1InvalidDNEFolderOutputToStream() throws Exception {
        String expected = testDir2 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE +
                findPrefix + sample1 + ": No such file or directory";
        assertEquals(expected, app.findFolderContent(fileNameC, srcDir, sample1));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 1 Valid Folder 1 Invalid folder i.e. file not in folder specified
    void testFindFolderContent1FileName1Valid1InvalidFolderFileNotFoundOutputToStream() throws Exception {
        String expected = testDir2 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + fileNameC;
        assertEquals(expected, app.findFolderContent(fileNameC, srcDir, testDir));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, >1 Folders All Invalid i.e. Folders do not exist
    void testFindFolderContent1FilenameMultipleInvalidFoldersDNEOutputToStream() throws Exception {
        String expected = findPrefix + sample1 + ": No such file or directory" + System.lineSeparator() +
                findPrefix + sample2 + ": No such file or directory";
        assertEquals(expected, app.findFolderContent(fileNameC, "sampleNotExist", "sampleNotExist2"));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, >1 Folders All Invalid i.e. File not in folders specified
    void testFindFolderContent1FileNameMultipleInvalidFoldersFileNotFoundOutputToStream() throws Exception {
        assertEquals("", app.findFolderContent("a.txt", testDir2, testDir3));
    }

    @Test
    // MUT: findFolderContent
    // Test Case: One FileName Specified, 0 Folders Specified
    void testFindFolderContent1FileNameNoFoldersSpecifiedOutputToStream(){
        assertThrows(FindException.class, () -> app.findFolderContent("a.txt", null));
    }

    // ===========================================
    // Test cases for find command parsing and running
    // ===========================================

    @Test
    // MUT: run
    // Test Case: stdout == null
    void testRunStdOutNullThrowException(){
        String[] args = {srcDir, suffix, fileNameC};
        assertThrows(FindException.class, () -> app.run(args, inputStream, null));
    }

    @Test
    void testRun3ArgsPositiveOutputToStream() throws AbstractApplicationException {
        String[] args = {srcDir, suffix, fileNameC};
        String expected = testDir2 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE;
        app.run(args, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    // Test Case: 4 Args Multiple FileName; FOLDERS -name FILENAME FILENAME
    void testRun4Args2FilenameThrowException(){
        String[] args = {srcDir, suffix, fileNameC, "d.txt"};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 4 Args Multiple Folder; FOLDER FOLDER -name FILENAME
    void testRun4ArgsPositiveMultipleFoldersOutputToStream() throws AbstractApplicationException {
        String[] args = {testDir2, testDir3, suffix, fileNameC};
        app.run(args, inputStream, outputStream);
        String expected = testDir2 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE +
                testDir3 + CHAR_FILE_SEP + fileNameC + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

    @Test
    // Test Case: 4 Args Multiple Folder Incorrect Order; -name FILENAME FOLDER FOLDER
    void testRun4ArgsMultipleFoldersIncorrectOrderThrowException() {
        String[] args = {suffix, fileNameC, srcDir, "lib"};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 3 Args Incorrect Order 1; -name FILENAME FOLDER
    void testRun3ArgsIncorrectOrder1ThrowException() {
        String[] args = {suffix, fileNameC, srcDir};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 3 Args Incorrect Order 2; FOLDER FILENAME -name
    void testRun3ArgsIncorrectOrder2ThrowException() {
        String[] args = {srcDir, fileNameC, suffix};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 3 Args Incorrect Order 3; -name FOLDER FILENAME
    void testRun3ArgsIncorrectOrder3ThrowException() {
        String[] args = {suffix, srcDir, fileNameC};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 3 Args Incorrect Order 4; FILENAME -name FOLDER
    void testRun3ArgsIncorrectOrder4OutputToStream() throws AbstractApplicationException {
        String[] args = {fileNameC, suffix, srcDir};
        String expected = findPrefix + fileNameC + ": " + ERR_FILE_NOT_FOUND + System.lineSeparator();
        app.run(args, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    // Test Case: 3 Args Incorrect Order 5; FILENAME FOLDER -name
    void testRun3ArgsIncorrectOrder5ThrowException() {
        String[] args = {fileNameC, srcDir, suffix};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 2 Args; FOLDER FILENAME
    void testRun2ArgsNoFlagThrowException() {
        String[] args = {srcDir, fileNameC};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 2 Args; FOLDER -name
    void testRun2ArgsNoFileNameThrowException(){
        String[] args = {srcDir, suffix};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 2 Args; -name FILENAME
    void testRun2ArgsNoFolderThrowException() {
        String[] args = {suffix, fileNameC};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 1 Arg; FOLDER
    void testRun1ArgFolderThrowException() {
        String[] args = {srcDir};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 1 Arg; -name
    void testRun1ArgFlagThrowException() {
        String[] args = {suffix};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    // Test Case: 1 Arg; FILENAME
    void testRun1ArgFileNameThrowException() {
        String[] args = {"-c.txt"};
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    void testRunEmptyArgsOutputToStream() {
        String[] args = { };
        assertThrows(FindException.class, () -> app.run(args, inputStream, outputStream));
    }

    @Test
    void testRunNullArgsOutputToStream() {
        assertThrows(FindException.class, () -> app.run(null, inputStream, outputStream));
    }

}
