package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.IOUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

class IOUtilsTest {

    private final static String TEST_TXT ="test.txt";
    private final static String NULL_TXT = "null/null.txt";
    private final static String NONE_TXT = "null/none.txt";

    @BeforeEach
    void setUp() {
        try {
            FileSystemUtils.createFile(TEST_TXT);
            try(OutputStream fileOutputStream = openOutputStream(TEST_TXT);
                Writer writer = new OutputStreamWriter(fileOutputStream)){
                writer.write("hello\nworld");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void openInputStreamFromExistFile() {
        assertDoesNotThrow(()->{ openInputStream(TEST_TXT); });
    }

    @Test
    void openInputStreamFromNotExistFile() {
        assertThrows(ShellException.class,()->{ openInputStream(NULL_TXT); });
    }

    @Test
    void openOutputStreamFromExistFile() {
        assertDoesNotThrow(()->{ openOutputStream(TEST_TXT); });
    }

    @Test
    void openOutputStreamFromNotExistFile() {
        assertThrows(ShellException.class,()->{ openOutputStream(NONE_TXT); });
    }

    @Test
    void closeSystemIn() {
        assertDoesNotThrow(()->{
            closeInputStream(System.in);
            return System.in.available();
        });
    }

    @Test
    void closeSystemOut() {
        assertDoesNotThrow(()->{
            closeOutputStream(System.out);
            System.out.print("");
        });
    }

    @Test
    void closeNullIn() {
        assertDoesNotThrow(()->{
            closeInputStream(null);
        });
    }

    @Test
    void closeNullOut() {
        assertDoesNotThrow(()->{
            closeOutputStream(null);
        });
    }

    @Test
    void closeNormalInputStream() {
        assertDoesNotThrow(()-> {
            closeInputStream(openInputStream(TEST_TXT));
        });
    }

    @Test
    void closeNormalOutputStream() {
        assertDoesNotThrow(()-> {
            closeOutputStream(openOutputStream(TEST_TXT));
        });
    }

    @Test
    void closeUnClosableInputStream() {
        assertThrows(ShellException.class, ()->{
            closeInputStream(new UnClosableInputStream(TEST_TXT));
        });
    }

    @Test
    void closeUnClosableOutputStream() {
        assertThrows(ShellException.class, ()->{
            closeOutputStream(new UnClosableOutputStream(TEST_TXT));
        });
    }

    @Test
    void getLinesFromTestFile() {
        List<String> result = new ArrayList<>(Arrays.asList("hello","world"));
        assertDoesNotThrow(()->{
            assertIterableEquals(result, getLinesFromInputStream(openInputStream(TEST_TXT)));
        });
    }

    @Test
    void getLinesFromNull() {
        assertDoesNotThrow(()->{
            assertNull(getLinesFromInputStream(null));
        });
    }

    @AfterEach
    void tearDown() {
        FileSystemUtils.deleteFileRecursive(new File(TEST_TXT));
        FileSystemUtils.deleteFileRecursive(new File(NULL_TXT));
        FileSystemUtils.deleteFileRecursive(new File(NONE_TXT));
        FileSystemUtils.deleteFileRecursive(new File("asset"+CHAR_FILE_SEP+TEST_TXT));
    }

    static class UnClosableInputStream extends FileInputStream {

        public UnClosableInputStream(String name) throws FileNotFoundException {
            super(name);
        }

        @Override
        public void close() throws IOException {
            throw new IOException("Not closable");
        }
    }

    static class UnClosableOutputStream extends FileOutputStream {

        public UnClosableOutputStream(String name) throws FileNotFoundException {
            super(name);
        }

        @Override
        public void close() throws IOException {
            throw new IOException("Not closable");
        }
    }

}
