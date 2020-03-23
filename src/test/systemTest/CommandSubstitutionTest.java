package systemTest;

import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

public class CommandSubstitutionTest extends AbstractIntegrationTest {

    private final String integrationDir = FileSystemUtils.joinPath("asset", "system-test", "ApplicationWithCommandSubstitution");

    @Override
    String getIntegrationDir() {
        return integrationDir;
    }
}
