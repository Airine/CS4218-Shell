package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

public class DiffApplication implements DiffInterface {

    @Override
    public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        if (fileNameA == null || fileNameB == null) {
            throw new DiffException(ERR_NULL_ARGS);
        }
        File fileA = IOUtils.resolveFilePath(fileNameA).toFile();
        File fileB = IOUtils.resolveFilePath(fileNameB).toFile();
        if (!fileA.exists() || !fileB.exists()){
            throw new DiffException(ERR_FILE_NOT_FOUND);
        }
        return null;
    }

    @Override
    public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        if (folderA == null || folderB == null) {
            throw new DiffException(ERR_NULL_ARGS);
        }
        File folderAA = IOUtils.resolveFilePath(folderA).toFile();
        File folderBB = IOUtils.resolveFilePath(folderB).toFile();
        if (!folderAA.exists() || !folderBB.exists()){
            throw new DiffException(ERR_FILE_NOT_FOUND);
        }
        return null;
    }

    @Override
    public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        return null;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
// todo
    }
}
