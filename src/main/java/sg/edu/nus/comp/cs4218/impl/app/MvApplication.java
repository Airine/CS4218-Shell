package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.parser.MvArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MvApplication implements MvInterface {

    private boolean isOverride = true;

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
        try {
            for (String oneFileName : fileName) {
                destFilePath = FileSystemUtils.joinPath(FileSystemUtils.getAbsolutePathName(destFolder),
                        new File(FileSystemUtils.getAbsolutePathName(oneFileName)).getName());
                if (FileSystemUtils.isSubDir(oneFileName, destFolder)) {
                    throw new MvException(destFolder + " is the sub dir of " + destFolder + " or they are the same file.");
                }
                File destFile = new File(destFilePath);
                if (isOverride && destFile.exists()) {
                    destFile.delete();
                }
                Files.move(Paths.get(FileSystemUtils.getAbsolutePathName(oneFileName)),
                        Paths.get(destFilePath));
            }
        } catch (FileAlreadyExistsException e) {
            throw (MvException) new MvException("A file with the same name already exists in "
                    + "directory '" + destFolder + "' and cannot be replaced.").initCause(e);
        } catch (AccessDeniedException e) {
            throw new MvException(ErrorConstants.ERR_NO_PERM + ":" + e.getFile());
        }
        return destFilePath;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws MvException {
        if (args == null) {
            throw new MvException(ErrorConstants.ERR_NULL_ARGS);
        }
        MvArgsParser mvArgsParser = new MvArgsParser();
        try {
            mvArgsParser.parse(args);
            String[] toMoveFiles = mvArgsParser.getToMoveFileName();
            if (toMoveFiles.length == 0) {
                throw new InvalidArgsException(ErrorConstants.ERR_MISSING_ARG);
            }
            String destPath = mvArgsParser.getDestFilePathName();
            if (new File(FileSystemUtils.getAbsolutePathName(destPath)).isDirectory()) {
                isOverride = mvArgsParser.isOverwrite();
                mvFilesToFolder(destPath, toMoveFiles);
            } else {
                if (toMoveFiles.length != 1) {
                    throw new InvalidArgsException(ErrorConstants.ERR_MISSING_ARG);
                }
                if (!mvArgsParser.isOverwrite() && new File(destPath).exists()) {
                    throw new MvException("Destination file '" + destPath + "' already exists and cannot be replaced.");
                }
                mvSrcFileToDestFile(toMoveFiles[0], destPath);
            }
        } catch (Exception e) {
            try {
                if (stdout == null) {
                    throw (MvException) new MvException("OutputStream not provided").initCause(e);
                }
                stdout.write(e.getMessage().getBytes());
            } catch (IOException ex) {
                throw (MvException) new MvException("Could not write to output stream").initCause(ex);
            }
        }

    }
}
