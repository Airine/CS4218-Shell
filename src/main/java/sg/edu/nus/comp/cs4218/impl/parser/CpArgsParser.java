package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.Iterator;

public class CpArgsParser extends ArgsParser {

    private String[] toCopyFileName;

    private String destFilePathName;

    public String[] getToCopyFileName() {
        if (toCopyFileName == null) {
            splitLastArgument();
        }
        return toCopyFileName;
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
            toCopyFileName = new String[0];
            return;
        }
        toCopyFileName = new String[argsSize - 1];
        Iterator<String> iterator = super.nonFlagArgs.iterator();
        for (int i = 0; i < argsSize - 1; i++) {
            toCopyFileName[i] = iterator.next();
        }
        destFilePathName = iterator.next();
    }


}
