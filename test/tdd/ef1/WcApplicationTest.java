package tdd.ef1;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.app.WcApplication;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WcApplicationTest {

    private WcApplication wcApplication;
    private InputStream inputStream = Mockito.mock(InputStream.class);
    private OutputStream outputStream;
    private final String LINES_FLAG = "-l";
    private final String WORDS_FLAG = "-w";
    private final String BYTES_FLAG = "-c";

    @BeforeEach
    void setUp() {
        wcApplication = new WcApplication();
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    @DisplayName("should throw exception if stdout is null")
    void throwsExceptionNullStdout() {
        assertThrows(WcException.class, () -> wcApplication.run(null, inputStream, null));
    }

    @Nested
    @DisplayName("countFromFilesTest")
    class countFromFilesTest {
        private final String FILENAME1 = "file1.txt";
        private final String FILENAME2 = "file2.txt";
        private File file1;
        private File file2;

        @BeforeEach
        void setUp() {
            try {
                file1 = new File(FILENAME1);
                file1.createNewFile();
                FileWriter fw1 = new FileWriter(FILENAME1);
                fw1.write("hello world" + System.lineSeparator() +
                        "sample file" + System.lineSeparator() +
                        "birds hello" + System.lineSeparator() +
                        "j testing" + System.lineSeparator() +
                        "TestiNG SAmplE output");
                fw1.close();
            } catch (IOException e) {
                System.out.println("File1 failed to be created.");
            }

            try {
                file2 = new File(FILENAME2);
                file2.createNewFile();
                FileWriter fw2 = new FileWriter(FILENAME2);
                fw2.write("afkjadslfj world" + System.lineSeparator() +
                        "slightly different output" + System.lineSeparator() +
                        "test" + System.lineSeparator() +
                        "this is a tesT" + System.lineSeparator() +
                        "TESTING is fun");
                fw2.close();
            } catch (IOException e) {
                System.out.println("File2 failed to be created.");
            }
        }

        @AfterEach
        void tearDown() {
            file1.delete();
            file2.delete();
        }

        @Test
        @DisplayName("should throw exception if null filename")
        void countFromFiles_throwsExceptionIfNullFileName() {
            assertThrows(Exception.class, () -> wcApplication.run(new String[]{null}, inputStream, outputStream));
        }

        @Test
        @DisplayName("should count only lines")
        void countFromFiles_countLines() throws AbstractApplicationException {
            String expected = "4 file1.txt";
            wcApplication.run(new String[]{LINES_FLAG, FILENAME1}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count only words")
        void countFromFiles_countWords() throws AbstractApplicationException {
            String expected = "11 file1.txt";
            wcApplication.run(new String[]{WORDS_FLAG, FILENAME1}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count only bytes")
        void countFromFiles_countBytes() throws AbstractApplicationException {
            String expected = "71 file1.txt";
            wcApplication.run(new String[]{BYTES_FLAG, FILENAME1}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count lines and bytes")
        void countFromFiles_countLinesAndBytes() throws AbstractApplicationException {
            String expected = "4      71 file1.txt";
            wcApplication.run(new String[]{LINES_FLAG, BYTES_FLAG, FILENAME1}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count lines, bytes, and words with all args")
        void countFromFiles_allArgs() throws AbstractApplicationException {
            String expected = "4      11      71 file1.txt";
            wcApplication.run(new String[]{WORDS_FLAG, LINES_FLAG, BYTES_FLAG, FILENAME1}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count lines, bytes, and words from multiple files")
        void countFromFiles_multiFiles() throws AbstractApplicationException {
            String expected = "4      11      71 file1.txt" + System.lineSeparator() +
                    "       4      13      81 file2.txt" + System.lineSeparator() +
                    "       8      24     152 total";
            wcApplication.run(new String[]{WORDS_FLAG, LINES_FLAG, BYTES_FLAG, FILENAME1, FILENAME2}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }
    }

    @Nested
    @DisplayName("countFromStdinTest")
    class countFromStdinTest {
        private String inputString = "testing testing this is a sample string\n" +
                "withlines and bytes and\n" +
                "more lines\n" +
                "asfassafasdf\n";
        private InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());

        @Test
        @DisplayName("should throw exception if null input stream")
        void countFromStdin_throwsExceptionIfNullInputStream() {
            assertThrows(Exception.class, () -> wcApplication.run(null, null, outputStream));
        }

        @Test
        @DisplayName("should display nothing with no args")
        void countFromStdin_noArgs() throws AbstractApplicationException {
            String expected = "0       0       0";
            wcApplication.run(null, new ByteArrayInputStream("".getBytes()), outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count only lines")
        void countFromStdin_countLines() throws AbstractApplicationException {
            String expected = "4";
            wcApplication.run(new String[]{LINES_FLAG}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count only words")
        void countFromStdin_countWords() throws AbstractApplicationException {
            String expected = "14";
            wcApplication.run(new String[]{WORDS_FLAG}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count only bytes")
        void countFromStdin_countBytes() throws AbstractApplicationException {
            String expected = "88";
            wcApplication.run(new String[]{BYTES_FLAG}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count lines and bytes")
        void countFromStdin_countWordsAndBytes() throws AbstractApplicationException {
            String expected = "14      88";
            wcApplication.run(new String[]{WORDS_FLAG, BYTES_FLAG}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }

        @Test
        @DisplayName("should count lines, bytes, and words with all args")
        void countFromStdin_allArgs() throws AbstractApplicationException {
            String expected = "4      14      88";
            wcApplication.run(new String[]{WORDS_FLAG, LINES_FLAG, BYTES_FLAG}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString().trim());
        }
    }
}