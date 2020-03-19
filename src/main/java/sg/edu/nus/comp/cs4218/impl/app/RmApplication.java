package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.parser.RmArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class RmApplication implements RmInterface {

    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileName) throws Exception {
        if (isEmptyFolder == null || isRecursive == null || fileName == null) {
            throw new Exception(ErrorConstants.ERR_NULL_ARGS);
        }
        if (fileName.length == 0) {
            throw new Exception(ErrorConstants.ERR_MISSING_ARG);
        }
        for (String oneFile : fileName) {
            File file = new File(oneFile);
            if (!file.exists()) {
                throw new RmException("File do not exist:" + oneFile);
            }
            if (!file.canWrite()) {
                throw new RmException("File may read only:" + oneFile);
            }
            if (file.isDirectory()) {
                if (isRecursive) {
                    if (!file.canRead()) {
                        throw new RmException("File can not read, so can not recursively delete its child files:" + oneFile);
                    }
                    FileSystemUtils.deleteFileRecursive(file);
                } else if (isEmptyFolder) {
                    if (file.listFiles() == null || file.listFiles().length == 0) {
                        file.delete();
                    } else {
                        throw new RmException("remove a non empty folder");
                    }
                } else {
                    throw new RmException("can not remove a folder");
                }
            } else {
                file.delete();
            }
        }
    }


    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws RmException {
        if (args == null || args.length == 0) {
            throw new RmException(ErrorConstants.ERR_NULL_ARGS);
        }
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
