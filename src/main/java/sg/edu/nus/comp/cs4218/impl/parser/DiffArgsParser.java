package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class DiffArgsParser extends ArgsParser{
    private final static char FLAG_SHOW_SAME = 's';
    private final static char FLAG_NO_BLANK = 'B';
    private final static char FLAG_IS_SIMPLE = 'q';

    public DiffArgsParser() {
        super();
        legalFlags.add(FLAG_SHOW_SAME);
        legalFlags.add(FLAG_NO_BLANK);
        legalFlags.add(FLAG_IS_SIMPLE);
    }

    public boolean isReportIdentical() { return flags.contains(FLAG_SHOW_SAME); }

    public boolean isIgnoreBlanks() { return flags.contains(FLAG_NO_BLANK); }

    public boolean isMsgDiff() { return flags.contains(FLAG_IS_SIMPLE); }

    public List<String> getDiffFile() { return nonFlagArgs; }
}
