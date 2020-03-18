package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.app.NewIOStream;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@Disabled
public abstract class AbstractIntegrationTest {
    private Shell shell;
    private InputStream inputStream;
    private PrintStream outputStream;
    private NewIOStream ioStream;

    private String originalCwd;


    @BeforeAll
    static void setUp() {
        try {
            TestFileUtils.createSomeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown() {
        TestFileUtils.rmCreatedFiles();
    }

    /**
     * App will change directory to this path and run relative test
     *
     * @return the integration test path
     */
    abstract String getIntegrationDir();

    @BeforeEach
    void setStdInAndOut() {
        inputStream = System.in;
        outputStream = System.out;
        try {
            ioStream = new NewIOStream(TestFileUtils.tempFileName1);
            System.setIn(ioStream.inputStream);
            System.setOut(new PrintStream(ioStream.outputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        shell = new ShellImpl();
        originalCwd = Environment.currentDirectory;
        Environment.currentDirectory = FileSystemUtils.getAbsolutePathName(getIntegrationDir());
    }


    @AfterEach
    void resumeStdInAndOut() {
        System.setIn(inputStream);
        System.setOut(outputStream);
        try {
            ioStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Environment.currentDirectory = originalCwd;
    }

    /**
     * change xxx.in to xxx.out
     */
    private String getCorrespondingFile(String name) {
        assert name.endsWith(".in");
        byte[] str = name.getBytes();
        byte[] bytes = new byte[str.length + 1];
        System.arraycopy(str, 0, bytes, 0, str.length);
        bytes[str.length - 2] = 'o';
        bytes[str.length - 1] = 'u';
        bytes[str.length] = 't';
        return new String(bytes);
    }

    /**
     * It will use xx.in as System.in and xx.out is its expect output, all these files
     * should be in a same folder.
     */
    @Test
    void testAndValidateStdOut() {
        File[] files = new File(Environment.currentDirectory).listFiles();
        Object[] testFileIn = Arrays.stream(Objects.requireNonNull(files))
                .filter(file -> file.getName().endsWith(".in"))
                .toArray();
        HashMap<String, File> map = new HashMap<>();
        for (Object file : files) {
            File objectFile = (File) file;
            map.put(objectFile.getName(), objectFile);
        }
        for (Object o : testFileIn) {
            File testFile = (File) o;
            String outFileName = getCorrespondingFile(testFile.getName());
            if (!map.containsKey(outFileName)) {
                continue;
            }
            File testStd = map.get(outFileName);
            try (NewIOStream ioStream = new NewIOStream(testFile.getAbsolutePath())) {
                this.outputStream.println("Start testing:" + testFile.getName());
                buildTestSuit(ioStream.inputStream, ioStream.outputStream);
                validResult(testStd.getAbsolutePath(), ioStream.outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * use to valid the result by comparing with output file,
     * typically, the answer should not exceed 1024 bytes
     *
     * @param stdOutPath   the file path which contains stand ouput
     * @param outputStream application output stream
     * @throws IOException
     */
    protected void validResult(String stdOutPath, OutputStream outputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stdOutPath)))) {

            char[] buf = new char[1024];
            int len = reader.read(buf);
            String expect = len < 0 ? "" : new String(buf, 0, len);
            assertEquals(expect, outputStream.toString(), "for test:" + stdOutPath);
            assertEquals(-1, reader.read(), "this stand output exceed 1024 bytes");
        }
    }

    /**
     * run some groups of tests in the inputStream
     *
     * @param inputStream  replace stdin
     * @param outputStream repalce stdout
     * @throws IOException
     */
    private void buildTestSuit(InputStream inputStream, OutputStream outputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String command;
            while ((command = reader.readLine()) != null) {
                if (StringUtils.isBlank(command)) {
                    OutputStream.write(STRING_NEWLINE.getbytes());
                    continue;
                }
                shell.parseAndEvaluate(command, outputStream);
            }
        } catch (ShellException | AbstractApplicationException e) {
            e.printStackTrace();
        }
    }
}
