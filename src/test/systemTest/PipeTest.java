package systemTest;

import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

public class PipeTest extends AbstractIntegrationTest {


    private final String integrationDir = FileSystemUtils.joinPath("asset", "system-test", "pipe");

    @Override
    String getIntegrationDir() {
        return integrationDir;
    }


}
