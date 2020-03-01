package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.NewIOStream;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IORedirectIntegrationTest {

    private Shell shell;
    private InputStream inputStream;
    private PrintStream outputStream;
    private NewIOStream ioStream;

    private String integrationDir = FileSystemUtils.joinPath("asset", "itest", "redirect");

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
    }

    @Test
    void testSimpleCommand() {
        String command = "echo pwd";
        Assertions.assertDoesNotThrow(() -> shell.parseAndEvaluate(command, ioStream.outputStream));
        assertEquals("pwd", ioStream.outputStream.toString());
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

    @Test
    void testSimpleRedirect() {
        File[] files = new File(this.integrationDir).listFiles();
        Object[] testFileIn = Arrays.stream(Objects.requireNonNull(files))
                .filter(file -> file.getName().endsWith(".in"))
                .toArray();
        HashMap<String, File> map = new HashMap<>();
        for (Object file : files) {
            File fo = (File) file;
            map.put(fo.getName(), fo);
        }
        for (Object o : testFileIn) {
            File testFile = (File) o;
            String outFileName = getCorrespondingFile(testFile.getName());
            if (!map.containsKey(outFileName)) {
                continue;
            }
            File testStd = map.get(outFileName);
            try (NewIOStream ioStream = new NewIOStream(testFile.getAbsolutePath())) {
                buildTestSuit(ioStream.inputStream, ioStream.outputStream);
                valideResult(testStd.getAbsolutePath(), ioStream.outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void valideResult(String stdOutPath, OutputStream outputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stdOutPath)))) {
            char[] buf = new char[1024];
            int len = reader.read(buf);
            assertEquals(new String(buf, 0, len), outputStream.toString(), "for test:" + stdOutPath);
        }
    }

    private void buildTestSuit(InputStream inputStream, OutputStream outputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String command;
            while ((command = reader.readLine()) != null) {
                if (StringUtils.isBlank(command)) {
                    continue;
                }
                shell.parseAndEvaluate(command, outputStream);
            }
        } catch (ShellException | AbstractApplicationException e) {
            e.printStackTrace();
        }
    }


}
