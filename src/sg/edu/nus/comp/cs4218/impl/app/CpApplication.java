package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.io.InputStream;
import java.io.OutputStream;

public class CpApplication implements CpInterface {
    @Override
    public String cpSrcFileToDestFile(String srcFile, String destFile) throws Exception {
        return null;
    }

    @Override
    public String cpFilesToFolder(String destFolder, String... fileName) throws Exception {
        return null;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

    }
}
