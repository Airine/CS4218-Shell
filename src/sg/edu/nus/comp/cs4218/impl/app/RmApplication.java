package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RmApplication implements RmInterface {


    /**
     * Remove the file. (It does not remove folder by default)
     *
     * @param isEmptyFolder Boolean option to delete a folder only if it is empty
     * @param isRecursive   Boolean option to recursively delete the folder contents (traversing
     *                      through all folders inside the specified folder)
     * @param fileName      Array of String of file names
     * @throws Exception
     */
    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileName) throws Exception {
        for (String oneFile : fileName) {
            File file = new File(oneFile);
            if (!file.exists()) {
                throw new Exception("File do not exist:" + oneFile);
            }
            if (file.isDirectory()) {
                if (isRecursive) {
                    FileSystemUtils.deleteFileRecursive(file);
                } else if (isEmptyFolder) {
                    if (Objects.requireNonNull(file.listFiles()).length != 0) {
                        throw new Exception("remove a non empty folder");
                    }
                    file.delete();
                } else {
                    throw new Exception("can not remove a folder");
                }
            } else {
                file.delete();
            }
        }
    }

    private String[] listToStringArray(List<String> list) {
        Object[] objects = list.toArray();
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            strings[i] = (String) objects[i];
        }
        return strings;
    }


    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

        List<String> list = new ArrayList<>();
        boolean isRecursive = false, isEmptyFolder = true;
        try {
            for (String arg : args) {
                if ("-r".equals(arg)) {
                    if (list.size() != 0) {
                        remove(isEmptyFolder, isRecursive, listToStringArray(list));
                        isEmptyFolder = false;
                        list = new LinkedList<>();
                    }
                    isRecursive = true;
                } else if ("-d".equals(arg)) {
                    if (list.size() != 0) {
                        remove(isEmptyFolder, isRecursive, (String[]) list.toArray());
                        isRecursive = false;
                        list = new LinkedList<>();
                    }
                    isEmptyFolder = true;
                } else {
                    list.add(arg);
                }
            }
            if (list.size() != 0) {
                remove(isEmptyFolder, isRecursive, listToStringArray(list));
            }
        } catch (Exception e) {
            try {
                stdout.write(e.getMessage().getBytes());
            } catch (IOException ex) {
                throw (RmException) new RmException("can not write to std out").initCause(e);
            }
        }
    }
}
