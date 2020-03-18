package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;


public class SemicolonTest extends AbstractIntegrationTest {


    private final String integrationDir = FileSystemUtils.joinPath("asset", "integration", "semicolon");

    @Override
    String getIntegrationDir() {
        return integrationDir;
    }


    @BeforeEach
    void setSimpleTest() {
        File file = new File(FileSystemUtils.joinPath(Environment.currentDirectory, "hello.txt"));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterEach
    void clearFiles() {
        File file = new File(FileSystemUtils.joinPath(Environment.currentDirectory, "hello.txt"));
        if (file.exists()) {
            file.delete();
        }
    }

}
