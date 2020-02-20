package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentUtils;
import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.parser.RmArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RmApplication implements RmInterface {

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


    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws RmException {

        RmArgsParser parser = new RmArgsParser();
        try {
            parser.parse(args);
            remove(parser.isEmptyFolder(), parser.isRecursive(), parser.files());
        } catch (Exception e) {
            try {
                stdout.write(e.getMessage().getBytes());
            } catch (IOException ex) {
                throw (RmException) new RmException("can not write to std out").initCause(e);
            }
        }
    }
}
