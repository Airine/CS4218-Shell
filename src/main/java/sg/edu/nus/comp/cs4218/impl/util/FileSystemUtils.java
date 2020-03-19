package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public final class FileSystemUtils {
    private FileSystemUtils() {
    }

    public static void deleteFileRecursive(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        deleteFileRecursive(f);
                    }
                }
            }
            file.delete(); //NOPMD do not need the return value
        }
    }

    public static String getAbsolutePathName(String name) {
        Path path = new File(name).toPath();
        if (!path.isAbsolute()) {
            path = Paths.get(Environment.currentDirectory, name);
        }
        return path.normalize().toString();
    }


    public static String joinPath(String... fileFolderName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileFolderName[0]);
        for (int i = 1; i < fileFolderName.length; i++) {
            stringBuilder.append(File.separator).append(fileFolderName[i]);
        }
        return stringBuilder.toString();
    }

    public static void createFile(String tempFileName) throws Exception {
        File file = new File(tempFileName);
        if (file.exists()) {
            throw new Exception("This test file already exist!" + tempFileName);
        } else {
            if (!file.createNewFile()) {
                throw new Exception("create file failed");
            }
        }
    }

    /**
     * Converts filename to absolute path, if initially was relative path
     *
     * @param fileName supplied by user
     * @return a String of the absolute path of the filename
     */
    public static String convertToAbsolutePath(String fileName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = Environment.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(fileName);
// check whether is has been absolute path
        if (Paths.get(convertedPath).isAbsolute()) {
            return convertedPath;
        }

        String newPath;
        if (convertedPath.length() >= home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }

    /**
     * Converts path provided by user into path recognised by the system
     *
     * @param path supplied by user
     * @return a String of the converted path
     */
    public static String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }
}
