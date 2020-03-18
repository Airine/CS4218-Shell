package sg.edu.nus.comp.cs4218;

public final class Environment {//NOPMD do not need to change

    /**
     * Java VM does not support changing the current working directory.
     * For this reason, we use EnvironmentUtils.currentDirectory instead.
     */
    public static volatile String currentDirectory = System.getProperty("user.dir");


    private Environment() {
    }

}
