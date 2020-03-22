package systemTest;

import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.IOException;
import java.io.OutputStream;

public class ApplicationRunnerTest extends AbstractIntegrationTest {

    private final String appRunnerDir = FileSystemUtils.joinPath("asset", "integration", "appRunner");

    @Override
    String getIntegrationDir() {
        return appRunnerDir;
    }

    @Override
    protected void validResult(String stdOutPath, OutputStream outputStream) throws IOException {
// do nothing
    }
}
