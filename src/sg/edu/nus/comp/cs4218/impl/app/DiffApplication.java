package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.DiffException;

import java.io.InputStream;
import java.io.OutputStream;

public class DiffApplication implements DiffInterface {
    @Override
    public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        return null;
    }

    @Override
    public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        return null;
    }

    @Override
    public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        return null;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

    }
}
