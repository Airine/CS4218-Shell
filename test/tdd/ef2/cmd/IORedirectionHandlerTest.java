package tdd.ef2.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.IORedirectionHandler;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_INPUT;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_OUTPUT;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
class IORedirectionHandlerTest {
    private static final Path DIRECTORY = Paths.get("test", "tdd", "util","dummyTestFolder","IORedirectionHandlerTestFolder");
    private static final String RESOURCE_PATH = DIRECTORY.toString() + File.separator;
    private static final String FILE_1 = RESOURCE_PATH + "file1.txt";
    private static final String FILE_2 = RESOURCE_PATH + "file2.txt";
    private static final String FILE_NON_EXISTENT = RESOURCE_PATH + "non_existent.txt";
    private static final String TEXT_FILES_IN_RESOURCE_FOLDER = RESOURCE_PATH + "*.txt";
    private static IORedirectionHandler redirHandler;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static ArgumentResolver argResolver = mock(ArgumentResolver.class);
    private static final String SHELL_EXCEPTION_PREFIX = "shell: ";
    private static final String STRING_REDIR_INPUT = String.valueOf(CHAR_REDIR_INPUT);
    private static final String STRING_REDIR_OUTPUT = String.valueOf(CHAR_REDIR_OUTPUT);

    @BeforeAll
    static void init() throws AbstractApplicationException, ShellException {
        doReturn(Collections.singletonList(FILE_1)).when(argResolver).resolveOneArgument(FILE_1);
        doReturn(Collections.singletonList(FILE_2)).when(argResolver).resolveOneArgument(FILE_2);
        doReturn(Collections.singletonList(FILE_NON_EXISTENT)).when(argResolver).resolveOneArgument(FILE_NON_EXISTENT);
        doReturn(Arrays.asList(FILE_1, FILE_2)).when(argResolver).resolveOneArgument(TEXT_FILES_IN_RESOURCE_FOLDER);
    }

    @BeforeEach
    void setUp() {
        inputStream = new ByteArrayInputStream("input".getBytes());
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() throws ShellException, IOException {
        IOUtils.closeInputStream(redirHandler.getInputStream());
        IOUtils.closeOutputStream(redirHandler.getOutputStream());
        Files.deleteIfExists(Paths.get(FILE_NON_EXISTENT));
    }

    @Test
    void testExtractRedirOptions_nullArgsList_throwsException() {
        redirHandler = new IORedirectionHandler(null, inputStream, outputStream, argResolver);
        String thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_SYNTAX, thrown);
    }

    @Test
    void testExtractRedirOptions_emptyArgsList_throwsException() {
        redirHandler = new IORedirectionHandler(Collections.emptyList(), inputStream, outputStream, argResolver);
        String thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_SYNTAX, thrown);
    }

    @Test
    void testExtractRedirOptions_multipleRedirInARow_throwsException() {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_INPUT, STRING_REDIR_INPUT, FILE_1), inputStream, outputStream, argResolver);
        String thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_SYNTAX, thrown);

        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_OUTPUT, STRING_REDIR_OUTPUT, FILE_1), inputStream, outputStream, argResolver);
        thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_SYNTAX, thrown);

        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_INPUT, STRING_REDIR_OUTPUT, FILE_1), inputStream, outputStream, argResolver);
        thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_SYNTAX, thrown);
    }

    @Test
    void testExtractRedirOptions_validArgsNoRedir_noException() {
        redirHandler = new IORedirectionHandler(Collections.singletonList(FILE_1), inputStream, outputStream, argResolver);
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());
        assertEquals(1, redirHandler.getNoRedirArgsList().size());
        assertEquals(FILE_1, redirHandler.getNoRedirArgsList().get(0));
    }

    @Test
    void testExtractRedirOptions_validArgsRedirInput_noException() throws IOException, ShellException {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_INPUT, FILE_1), inputStream, outputStream, argResolver);
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());
        assertEquals(0, redirHandler.getNoRedirArgsList().size());
        assertNotEquals(inputStream, redirHandler.getInputStream());
        assertEquals(IOUtils.openInputStream(FILE_1).read(), redirHandler.getInputStream().read());
    }

    @Test
    void testExtractRedirOptions_validArgsRedirOutput_noException() {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_OUTPUT, FILE_1), inputStream, outputStream, argResolver);
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());
        assertEquals(0, redirHandler.getNoRedirArgsList().size());
        assertNotEquals(outputStream, redirHandler.getOutputStream());
    }

    @Test
    void testExtractRedirOptions_validArgsRedirInputAndOutput_noException() throws ShellException, IOException {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_INPUT, FILE_1, STRING_REDIR_OUTPUT, FILE_2), inputStream, outputStream, argResolver);
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());
        assertEquals(0, redirHandler.getNoRedirArgsList().size());
        assertNotEquals(inputStream, redirHandler.getInputStream());
        assertEquals(IOUtils.openInputStream(FILE_1).read(), redirHandler.getInputStream().read());
        assertNotEquals(outputStream, redirHandler.getOutputStream());
    }

    @Test
    void testExtractRedirOptions_multipleInputStreams_throwsException() {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_INPUT, FILE_1, STRING_REDIR_INPUT, FILE_2), inputStream, outputStream, argResolver);
        String thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_MULTIPLE_STREAMS, thrown);
    }

    @Test
    void testExtractRedirOptions_multipleOutputStreams_throwsException() {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_OUTPUT, FILE_1, STRING_REDIR_OUTPUT, FILE_2), inputStream, outputStream, argResolver);
        String thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_MULTIPLE_STREAMS, thrown);
    }

    @Test
    void testExtractRedirOptions_multipleFilesAfterResolve_throwsException() {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_INPUT, TEXT_FILES_IN_RESOURCE_FOLDER), inputStream, outputStream, argResolver);
        String thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_SYNTAX, thrown);
    }

    @Test
    void testExtractRedirOptions_nonExistentInputFile_throwsException() {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_INPUT, FILE_NON_EXISTENT), inputStream, outputStream, argResolver);
        String thrown = assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions()).getMessage();
        assertEquals(SHELL_EXCEPTION_PREFIX + ERR_FILE_NOT_FOUND, thrown);
    }

    @Test
    void testExtractRedirOptions_nonExistentOutputFile_createsFile_noException() {
        redirHandler = new IORedirectionHandler(Arrays.asList(STRING_REDIR_OUTPUT, FILE_NON_EXISTENT), inputStream, outputStream, argResolver);
        assertFalse(new File(FILE_NON_EXISTENT).exists());
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());
        assertTrue(new File(FILE_NON_EXISTENT).exists());
    }
}
