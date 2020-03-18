package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.parser.MvArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MvApplication implements MvInterface {
    @Override
    public String mvSrcFileToDestFile(String srcFile, String destFile) throws Exception {
        String destFilePath = FileSystemUtils.getAbsolutePathName(destFile);
        Files.move(Paths.get(FileSystemUtils.getAbsolutePathName(srcFile)),
                Paths.get(destFilePath));
        return destFilePath;
    }

    @Override
    public String mvFilesToFolder(String destFolder, String... fileName) throws Exception {
        String destFilePath = null;
        for (String oneFileName : fileName) {
            destFilePath = FileSystemUtils.joinPath(FileSystemUtils.getAbsolutePathName(destFolder),
                    new File(FileSystemUtils.getAbsolutePathName(oneFileName)).getName());
            Files.move(Paths.get(FileSystemUtils.getAbsolutePathName(oneFileName)),
                    Paths.get(destFilePath));
        }
        return destFilePath;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws MvException {
        MvArgsParser mvArgsParser = new MvArgsParser();
        try {
            mvArgsParser.parse(args);
            String[] toMoveFiles = mvArgsParser.getToMoveFileName();
            if (toMoveFiles.length == 0) {
                throw new InvalidArgsException(ErrorConstants.ERR_MISSING_ARG);
            }
            String destPath = mvArgsParser.getDestFilePathName();
            if (new File(FileSystemUtils.getAbsolutePathName(destPath)).isDirectory()) {
                mvFilesToFolder(destPath, toMoveFiles);
            } else {
                if (toMoveFiles.length != 1) {
                    throw new InvalidArgsException(ErrorConstants.ERR_MISSING_ARG);
                }
                if (mvArgsParser.isOverwrite()) {
                    new File(destPath).delete();//todo
                }
                mvSrcFileToDestFile(toMoveFiles[0], destPath);
            }
        } catch (Exception e) {
            try {
                stdout.write(e.getMessage().getBytes());
            } catch (IOException ex) {
                throw (MvException) new MvException(ex.getMessage()).initCause(ex);
            }
        }

    }
}
