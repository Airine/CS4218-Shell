package sg.edu.nus.comp.cs4218;

public final class EnvironmentUtils {

    /**
     * Java VM does not support changing the current working directory.
     * For this reason, we use EnvironmentUtils.currentDirectory instead.
     */
    public static volatile String currentDirectory = System.getProperty("user.dir");


    private EnvironmentUtils() {
    }

}
