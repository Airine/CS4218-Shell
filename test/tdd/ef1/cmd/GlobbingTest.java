package tdd.ef1.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
class GlobbingTest {
    private static final Path DIRECTORY = Paths.get("test", "tdd","util", "dummyTestFolder", "GlobbingTestFolder");
    private static final String RESOURCE_PATH = DIRECTORY.toString() + File.separator;
    private static final String FOLDER_WITH_FILE = RESOURCE_PATH + "folderWithFile";
    private static final String FOLDER_WITH_FILE_PATH = FOLDER_WITH_FILE + File.separator;
    private static final String FILE_1 = RESOURCE_PATH + "file1.txt";
    private static final String FILE_2 = RESOURCE_PATH + "file2.tat";
    private static final String FILE_3 = RESOURCE_PATH + "file3.taa";
    private static final String FILE_4 = RESOURCE_PATH + "file4.aat";
    private static final String FILE_5 = RESOURCE_PATH + "file5.txt";
    private static final String FWF_FILE_1 = FOLDER_WITH_FILE_PATH + "file1.txt";
    private static final String FWF_FILE_2 = FOLDER_WITH_FILE_PATH + "file2.tat";
    private static final String FWF_FILE_3 = FOLDER_WITH_FILE_PATH + "file3.taa";
    private static final String FWF_FILE_4 = FOLDER_WITH_FILE_PATH + "file4.aat";
    private static final String FWF_FILE_5 = FOLDER_WITH_FILE_PATH + "file5.txt";
    private static String emptyFolder;
    private static Path tempDir;

    private ArgumentResolver argumentResolver;

    @BeforeAll
    static void init() throws IOException {
        tempDir = Files.createTempDirectory(DIRECTORY.toAbsolutePath(), "emptyFolder");
        emptyFolder = RESOURCE_PATH + tempDir.getFileName();
    }

    @AfterAll
    static void tearDown() {
        tempDir.toFile().deleteOnExit();
    }

    @BeforeEach
    void setUp() {
        argumentResolver = new ArgumentResolver();
    }

    @Test
    void testResolveOneArgument_singleAsteriskOnly() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + "*";
        List<String> expected = Arrays.asList(emptyFolder, FOLDER_WITH_FILE, FILE_1, FILE_2, FILE_3, FILE_4, FILE_5);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_singleAsterisk_fileExtension_exist() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + "*.txt";
        List<String> expected = Arrays.asList(FILE_1, FILE_5);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_singleAsterisk_fileExtension_nonExistent() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + "*.t";
        List<String> expected = Arrays.asList(input);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_multipleAsterisksInARow_workTheSameAsSingleAsterisk() throws AbstractApplicationException, ShellException {
        // Double asterisks
        String input = RESOURCE_PATH + "**";
        List<String> expected = Arrays.asList(emptyFolder, FOLDER_WITH_FILE, FILE_1, FILE_2, FILE_3, FILE_4, FILE_5);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        // Multiple asterisks
        input = RESOURCE_PATH + "*******";
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_multipleAsterisk_fileExtension() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + "*.t*";
        List<String> expected = Arrays.asList(FILE_1, FILE_2, FILE_3, FILE_5);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        input = RESOURCE_PATH + "*.t*t";
        expected = Arrays.asList(FILE_1, FILE_2, FILE_5);
        Collections.sort(expected);
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        input = RESOURCE_PATH + "*.*a*";
        expected = Arrays.asList(FILE_2, FILE_3, FILE_4);
        Collections.sort(expected);
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }


    @Test
    void testResolveOneArgument_singleAsterisk_emptyFolder() throws AbstractApplicationException, ShellException {
        String input = emptyFolder + File.separator + "*";
        // Not sure about the expected output
        List<String> expected = Arrays.asList(input);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_singleAsterisk_nonExistentFolder() throws AbstractApplicationException, ShellException {
        String input = RESOURCE_PATH + File.separator + "nonExistent" + File.separator + "*";
        List<String> expected = Arrays.asList(input);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_singleAsterisk_folderWithFile() throws AbstractApplicationException, ShellException {
        String input = FOLDER_WITH_FILE_PATH + "*";
        List<String> expected = Arrays.asList(FWF_FILE_1, FWF_FILE_2, FWF_FILE_3, FWF_FILE_4, FWF_FILE_5);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_singleAsterisk_folderWithFile_fileExtension_exist() throws AbstractApplicationException, ShellException {
        String input = FOLDER_WITH_FILE_PATH + "*.txt";
        List<String> expected = Arrays.asList(FWF_FILE_1, FWF_FILE_5);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_singleAsterisk_folderWithFile_fileExtension_nonExistent() throws AbstractApplicationException, ShellException {
        String input = FOLDER_WITH_FILE_PATH + "*.t";
        List<String> expected = Arrays.asList(input);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_multipleAsterisksInARow_folderWithFile_workTheSameAsSingleAsterisk() throws AbstractApplicationException, ShellException {
        // Double asterisks
        String input = FOLDER_WITH_FILE_PATH + "**";
        List<String> expected = Arrays.asList(FWF_FILE_1, FWF_FILE_2, FWF_FILE_3, FWF_FILE_4, FWF_FILE_5);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        // Multiple asterisks
        input = FOLDER_WITH_FILE_PATH + "*******";
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }

    @Test
    void testResolveOneArgument_multipleAsterisk_folderWithFile_fileExtension() throws AbstractApplicationException, ShellException {
        String input = FOLDER_WITH_FILE_PATH + "*.t*";
        List<String> expected = Arrays.asList(FWF_FILE_1, FWF_FILE_2, FWF_FILE_3, FWF_FILE_5);
        Collections.sort(expected);
        List<String> actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        input = FOLDER_WITH_FILE_PATH + "*.t*t";
        expected = Arrays.asList(FWF_FILE_1, FWF_FILE_2, FWF_FILE_5);
        Collections.sort(expected);
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);

        input = FOLDER_WITH_FILE_PATH + "*.*a*";
        expected = Arrays.asList(FWF_FILE_2, FWF_FILE_3, FWF_FILE_4);
        Collections.sort(expected);
        actual = argumentResolver.resolveOneArgument(input);
        assertEquals(expected, actual);
    }
}
