package sg.edu.nus.comp.cs4218.impl.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import sg.edu.nus.comp.cs4218.EnvironmentUtils;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

public class IORedirectIntegrationTest extends AbstractIntegrationTest {

    private final String integrationDir = FileSystemUtils.joinPath("asset", "integration", "redirect");

    @Override
    String getIntegrationDir() {
        return integrationDir;
    }

    @BeforeEach
    void setSimpleTest() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(
                FileSystemUtils.joinPath(EnvironmentUtils.currentDirectory, "simple.in")
        )) {
            fileOutputStream.write("echo helloworld > hello.txt".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void clearFiles(){
        File file = new File( FileSystemUtils.joinPath(EnvironmentUtils.currentDirectory, "hello.txt"));
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    protected void validResult(String stdOutPath, OutputStream outputStream) throws IOException {

        assertEquals("", outputStream.toString());
        try (FileInputStream reader = new FileInputStream(
                FileSystemUtils.joinPath(EnvironmentUtils.currentDirectory, "hello.txt"))) {
            byte[] buf = new byte[16];
            int len = reader.read(buf);
            assertEquals(new String(buf, 0, len), "helloworld");
            assertEquals(-1, reader.read(), "this stand output exceed bytes");
        }

    }
}
