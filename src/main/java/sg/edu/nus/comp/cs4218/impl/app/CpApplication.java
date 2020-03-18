package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.CpArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CpApplication implements CpInterface {
    @Override
    public String cpSrcFileToDestFile(String srcFile, String destFile) throws Exception {
        String destFilePath = FileSystemUtils.getAbsolutePathName(destFile);
        Files.copy(Paths.get(FileSystemUtils.getAbsolutePathName(srcFile)),
                Paths.get(destFilePath));
        return destFilePath;
    }

    @Override
    public String cpFilesToFolder(String destFolder, String... fileName) throws Exception {
        String destFilePath;
        for (String oneFileName : fileName) {
            destFilePath = FileSystemUtils.joinPath(
                    FileSystemUtils.getAbsolutePathName(destFolder),
                    new File(FileSystemUtils.getAbsolutePathName(oneFileName))
                            .getName());
            Files.copy(Paths.get(FileSystemUtils.getAbsolutePathName(oneFileName)),
                    Paths.get(destFilePath));
        }
        return destFolder;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CpException {
        if (args == null) {
            throw new CpException(ErrorConstants.ERR_NULL_ARGS);
        }
        if (args.length < 2) {
            throw new CpException((ErrorConstants.ERR_NO_ARGS));
        }
        CpArgsParser parser = new CpArgsParser();
        try {
            parser.parse(args);
            String destPath = FileSystemUtils.getAbsolutePathName(parser.getDestFilePathName());
            String[] toCopy = parser.getToCopyFileName();
            if (!new File(destPath).isDirectory() ) {
                if (toCopy.length != 1) {
                    throw new CpException(ErrorConstants.ERR_TOO_MANY_ARGS);
                }
                cpFilesToFolder(toCopy[0], destPath);
            } else {
                cpFilesToFolder(destPath, toCopy);
            }
        } catch (Exception e) {
            try {
                stdout.write(e.getMessage().getBytes());
            } catch (IOException ex) {
                throw (CpException) new CpException(ErrorConstants.ERR_IO_EXCEPTION).initCause(ex);
            }
        }


    }
}
