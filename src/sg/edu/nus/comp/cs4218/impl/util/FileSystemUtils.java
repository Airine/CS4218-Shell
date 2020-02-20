package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.EnvironmentUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class FileSystemUtils {
    private FileSystemUtils() {
    }

    public static void deleteFileRecursive(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    deleteFileRecursive(f);
                }
            }
            file.delete(); //NOPMD do not need the return value
        }
    }

    public static String getAbsolutePathName(String name) {
        Path path = new File(name).toPath();
        if (!path.isAbsolute()) {
            path = Paths.get(EnvironmentUtils.currentDirectory, name);
        }
        return path.normalize().toString();
    }

}
