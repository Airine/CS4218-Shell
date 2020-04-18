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
import java.nio.file.*;

public class MvApplication implements MvInterface {

    /**
     * default behaviour is override the existing file
     */
    private boolean isOverride = true;

    @Override
    public String mvSrcFileToDestFile(String srcFile, String destFile) throws MvException {
        String destFilePath = FileSystemUtils.getAbsolutePathName(destFile);
        try {
            if (isOverride && new File(destFilePath).exists()) {
                new File(destFile).delete();
            }
            Files.move(Paths.get(FileSystemUtils.getAbsolutePathName(srcFile)),
                    Paths.get(destFilePath));
        } catch (NoSuchFileException e) {
            throw new MvException(ErrorConstants.ERR_FILE_NOT_FOUND + ":" + e.getMessage());
        } catch (FileAlreadyExistsException e) {
            throw new MvException("target file has existed:" + e.getMessage());

        } catch (IOException e) {
            throw new MvException(e.getMessage());
        }
        return destFilePath;
    }


    @Override
    public String mvFilesToFolder(String destFolder, String... fileName) throws MvException {
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
        } catch (NoSuchFileException e) {
            throw new MvException(ErrorConstants.ERR_FILE_NOT_FOUND + ":" + e.getMessage());
        } catch (IOException e) {
            throw new MvException(e.getMessage());
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
            isOverride = mvArgsParser.isOverwrite();
            if (new File(FileSystemUtils.getAbsolutePathName(destPath)).isDirectory()) {
                mvFilesToFolder(destPath, toMoveFiles);
            } else {
                if (toMoveFiles.length != 1) {
                    throw new InvalidArgsException(ErrorConstants.ERR_MISSING_ARG);
                }
                if (!isOverride && new File(destPath).exists()) {
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
        } finally {
            isOverride = true;
        }

    }
}
