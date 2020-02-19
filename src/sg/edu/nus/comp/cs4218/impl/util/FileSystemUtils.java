package sg.edu.nus.comp.cs4218.impl.util;

import java.io.File;
import java.util.Objects;

public class FileSystemUtils {

    public static void deleteFileRecursive(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    deleteFileRecursive(f);
                }
            }
            file.delete();
        }
    }


}
