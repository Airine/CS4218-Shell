package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.Iterator;

public class MvArgsParser extends ArgsParser {
    private final static char OVERWRITE_FLAG = 'n';
    private String[] toMoveFileName;

    private String destFilePathName;

    public MvArgsParser() {
        super();
        super.legalFlags.add(OVERWRITE_FLAG);
    }

    public boolean isOverwrite() {
        return super.flags.contains(OVERWRITE_FLAG);
    }

    public String[] getToMoveFileName() {
        if (toMoveFileName == null) {
            splitLastArgument();
        }
        return toMoveFileName;
    }

    public String getDestFilePathName() {
        if (destFilePathName == null) {
            splitLastArgument();
        }
        return destFilePathName;
    }

    private void splitLastArgument() {
        int argsSize = super.nonFlagArgs.size();
        if (argsSize < 2) {
            toMoveFileName = new String[0];
            return;
        }
        toMoveFileName = new String[argsSize - 1];
        Iterator<String> iterator = super.nonFlagArgs.iterator();
        for (int i = 0; i < argsSize - 1; i++) {
            toMoveFileName[i] = iterator.next();
        }
        destFilePathName = iterator.next();
    }

}
