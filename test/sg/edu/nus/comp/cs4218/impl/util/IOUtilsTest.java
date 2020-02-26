package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.IOUtils.*;

class IOUtilsTest {

    private final String testFileName ="test.txt";
    private final String notExistFileName1 = "null/null.txt";
    private final String notExistFileName2 = "null/none.txt";

    @BeforeEach
    void setUp() {
        try {
            FileSystemUtils.createTestFile(testFileName);
            OutputStream fileOutputStream = openOutputStream(testFileName);
            Writer writer = new OutputStreamWriter(fileOutputStream);
            writer.write("hello\nworld");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void openInputStreamFromExistFile() {
        assertDoesNotThrow(()->{ openInputStream(testFileName); });
    }

    @Test
    void openInputStreamFromNotExistFile() {
        assertThrows(ShellException.class,()->{ openInputStream(notExistFileName1); });
    }

    @Test
    void openOutputStreamFromExistFile() {
        assertDoesNotThrow(()->{ openOutputStream(testFileName); });
    }

    @Test
    void openOutputStreamFromNotExistFile() {
        assertThrows(ShellException.class,()->{ openOutputStream(notExistFileName2); });
    }

    @Test
    void closeSystemIn() {
        assertDoesNotThrow(()->{
            closeInputStream(System.in);
            assertEquals(0, System.in.available());
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
            closeInputStream(openInputStream(testFileName));
        });
    }

    @Test
    void closeNormalOutputStream() {
        assertDoesNotThrow(()-> {
            closeOutputStream(openOutputStream(testFileName));
        });
    }

    @Test
    void closeUnClosableInputStream() {
        assertThrows(ShellException.class, ()->{
            closeInputStream(new unClosableInputStream(testFileName));
        });
    }

    @Test
    void closeUnClosableOutputStream() {
        assertThrows(ShellException.class, ()->{
            closeOutputStream(new unClosableOutputStream(testFileName));
        });
    }

    @Test
    void getLinesFromTestFile() {
        List<String> result = new ArrayList<>(Arrays.asList("hello","world"));
        assertDoesNotThrow(()->{
            assertIterableEquals(result, getLinesFromInputStream(openInputStream(testFileName)));
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
        FileSystemUtils.deleteFileRecursive(new File(testFileName));
        FileSystemUtils.deleteFileRecursive(new File(notExistFileName1));
        FileSystemUtils.deleteFileRecursive(new File(notExistFileName2));
    }

    static class unClosableInputStream extends FileInputStream {

        public unClosableInputStream(String name) throws FileNotFoundException {
            super(name);
        }

        @Override
        public void close() throws IOException {
            throw new IOException("Not closable");
        }
    }

    static class unClosableOutputStream extends FileOutputStream {

        public unClosableOutputStream(String name) throws FileNotFoundException {
            super(name);
        }

        @Override
        public void close() throws IOException {
            throw new IOException("Not closable");
        }
    }

}
