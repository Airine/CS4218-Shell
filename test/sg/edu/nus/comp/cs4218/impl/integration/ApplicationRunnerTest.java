package sg.edu.nus.comp.cs4218.impl.integration;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class ApplicationRunnerTest extends AbstractIntegrationTest{

    private static final String appRunnerDir = "assets" + CHAR_FILE_SEP + "integration" + CHAR_FILE_SEP + "appRunner" + CHAR_FILE_SEP;

    @Override
    String getIntegrationDir() {
        return appRunnerDir;
    }
}
