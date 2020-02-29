package sg.edu.nus.comp.cs4218.impl.app.args;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_RANGE;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class CutArguments {

    public static final char CHAR_CUTBYCHAR = 'c';
    public static final char CHAR_CUTBYBYTE = 'b';
    private final List<String> files;
    private boolean charPo, bytePo, range;
    private int startIdx, endIdx;

    public CutArguments() {
        this.charPo = false;
        this.bytePo = false;
        this.range = false;
        this.startIdx = 0;
        this.endIdx = 0;
        this.files = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `cut` application.
     *
     * @param args Array of arguments to parse
     * @throws Exception
     */
    public void parse(String... args) throws Exception {
        if (args.length < 2) {
            throw new Exception(ERR_NO_ARGS);
        }
        // See if flag is invalid
        if (args[0].charAt(0) == CHAR_FLAG_PREFIX && args[0].charAt(1) == CHAR_CUTBYCHAR) {
            charPo = true;
        } else if (args[0].charAt(0) == CHAR_FLAG_PREFIX && args[0].charAt(1) == CHAR_CUTBYBYTE){
            bytePo = true;
        } else {
            throw new Exception(ERR_INVALID_FLAG);
        }

        // Parse indexes
        if (args[1].contains("-")){
            int begin = Integer.parseInt(args[1].substring(0,args[1].indexOf('-')));
            int end = Integer.parseInt(args[1].substring(args[1].indexOf('-')+1));
            if (begin > end){
                throw new Exception(ERR_INVALID_RANGE);
            }
            range = true;
            startIdx = begin;
            endIdx = end;
        } else if (args[1].contains(",")){
            startIdx = Integer.parseInt(args[1].substring(0,args[1].indexOf(',')));
            endIdx = Integer.parseInt(args[1].substring(args[1].indexOf(',')+1));
        } else {
            startIdx = Integer.parseInt(args[1]);
        }

        // Parse files
        for (int index = 2; index < args.length; ++index) {
            this.files.add(args[index].trim());
        }
    }

    public List<String> getFiles() {
        return files;
    }

    public boolean isCharPo() {
        return charPo;
    }

    public boolean isBytePo() {
        return bytePo;
    }

    public int getStartIdx() {
        return startIdx;
    }

    public boolean isRange() {
        return range;
    }

    public int getEndIdx() {
        return endIdx;
    }
}
