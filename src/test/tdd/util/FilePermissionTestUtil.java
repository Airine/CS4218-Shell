package tdd.util;

import java.io.File;
import java.io.IOException;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.LongVariable"})
public class FilePermissionTestUtil {
    public static final String RESOURCE_DIR = "file_permission" + CHAR_FILE_SEP;
    public static final String NO_PERMISSION_FILE = "no_permission.txt";
    public static final String READ_ONLY_FILE = "read_only.txt";
    public static final String WRITE_ONLY_FILE = "write_only.txt";
    public static final String EXECUTE_ONLY_FILE = "execute_only.txt";
    public static final String NO_WRITE_FILE = "no_write.txt";
    public static final String NO_PERMISSION_DIR = "no_permission_dir";
    public static final String READ_ONLY_DIR = "read_only_dir";
    public static final String WRITE_ONLY_DIR = "write_only_dir";
    public static final String EXECUTE_ONLY_DIR = "execute_only_dir";
    public static final String NO_WRITE_DIR = "no_write_dir";

    public static final String RESOURCE_DIR_PATH = System.getProperty("user.dir") + CHAR_FILE_SEP + RESOURCE_DIR;
    public static final String NO_PERMISSION_FILE_PATH = RESOURCE_DIR_PATH + NO_PERMISSION_FILE;
    public static final String READ_ONLY_FILE_PATH = RESOURCE_DIR_PATH + READ_ONLY_FILE;
    public static final String WRITE_ONLY_FILE_PATH = RESOURCE_DIR_PATH + WRITE_ONLY_FILE;
    public static final String EXECUTE_ONLY_FILE_PATH = RESOURCE_DIR_PATH + EXECUTE_ONLY_FILE;
    public static final String NO_WRITE_FILE_PATH = RESOURCE_DIR_PATH + NO_WRITE_FILE;
    public static final String NO_PERMISSION_DIR_PATH = RESOURCE_DIR_PATH + NO_PERMISSION_DIR;
    public static final String READ_ONLY_DIR_PATH = RESOURCE_DIR_PATH + READ_ONLY_DIR;
    public static final String WRITE_ONLY_DIR_PATH = RESOURCE_DIR_PATH + WRITE_ONLY_DIR;
    public static final String EXECUTE_ONLY_DIR_PATH = RESOURCE_DIR_PATH + EXECUTE_ONLY_DIR;
    public static final String NO_WRITE_DIR_PATH = RESOURCE_DIR_PATH + NO_WRITE_DIR;

    public File resourceDir;
    public File noPermissionFile;
    public File readOnlyFile;
    public File writeOnlyFile;
    public File executeOnlyFile;
    public File noWriteFile;
    public File noPermissionDir;
    public File readOnlyDir;
    public File writeOnlyDir;
    public File executeOnlyDir;
    public File noWriteDir;

    public FilePermissionTestUtil() {
        resourceDir = new File(RESOURCE_DIR_PATH);
        noPermissionFile = new File(NO_PERMISSION_FILE_PATH);
        readOnlyFile = new File(READ_ONLY_FILE_PATH);
        writeOnlyFile = new File(WRITE_ONLY_FILE_PATH);
        executeOnlyFile = new File(EXECUTE_ONLY_FILE_PATH);
        noWriteFile = new File(NO_WRITE_FILE_PATH);
        noPermissionDir = new File(NO_PERMISSION_DIR_PATH);
        readOnlyDir = new File(READ_ONLY_DIR_PATH);
        writeOnlyDir = new File(WRITE_ONLY_DIR_PATH);
        executeOnlyDir = new File(EXECUTE_ONLY_DIR_PATH);
        noWriteDir = new File(NO_WRITE_DIR_PATH);
    }

    public void createTestEnv() {
        resourceDir.mkdir();
        noPermissionDir.mkdir();
        readOnlyDir.mkdir();
        writeOnlyDir.mkdir();
        executeOnlyDir.mkdir();
        noWriteDir.mkdir();

        try {
            noPermissionFile.createNewFile();
            readOnlyFile.createNewFile();
            writeOnlyFile.createNewFile();
            executeOnlyFile.createNewFile();
            noWriteFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        readOnlyFile.setReadOnly();
        readOnlyDir.setExecutable(false);
        readOnlyDir.setWritable(false);

        writeOnlyFile.setExecutable(false);
        writeOnlyFile.setReadable(false);
        writeOnlyDir.setExecutable(false);
        writeOnlyDir.setReadable(false);

        executeOnlyFile.setWritable(false);
        executeOnlyFile.setReadable(false);
        executeOnlyDir.setWritable(false);
        executeOnlyDir.setReadable(false);

        noPermissionFile.setExecutable(false);
        noPermissionFile.setReadable(false);
        noPermissionFile.setWritable(false);
        noPermissionDir.setExecutable(false);
        noPermissionDir.setReadable(false);
        noPermissionDir.setWritable(false);

        noWriteFile.setWritable(false);
        noWriteDir.setWritable(false);
    }

    public void removeTestEnv() {
        noPermissionFile.delete();
        readOnlyFile.delete();
        writeOnlyFile.delete();
        executeOnlyFile.delete();
        noWriteFile.delete();

        noPermissionDir.delete();
        readOnlyDir.delete();
        writeOnlyDir.delete();
        executeOnlyDir.delete();
        noWriteDir.delete();
        resourceDir.delete();
    }
}
