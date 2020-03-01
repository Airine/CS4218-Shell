package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.CutException;

import java.io.InputStream;

public interface CutInterface extends Application {

    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param isRange  Boolean option to perform range-based cut
     * @param startIdx index to begin cut
     * @param endIdx   index to end cut
     * @param fileName Array of String of file names
     * @return The expected result
     * @throws CutException Throw exception caused by CutApplication
     */
    String cutFromFiles(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx,
                        String... fileName) throws CutException;


    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param isRange  Boolean option to perform range-based cut
     * @param startIdx index to begin cut
     * @param endIdx   index to end cut
     * @param stdin    InputStream containing arguments from Stdin
     * @return The expected result
     * @throws CutException Throw exception caused by CutApplication
     */
    String cutFromStdin(Boolean isCharPo, Boolean isBytePo, Boolean isRange, int startIdx, int endIdx,
                        InputStream stdin) throws CutException;
}
