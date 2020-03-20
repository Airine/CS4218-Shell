package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class DiffArgsParser extends ArgsParser{
    private final static char FLAG_REP_IDENT = 's';
    private final static char FLAG_IGN_BLANK = 'B';
    private final static char FLAG_MSG_DIFF = 'q';

    public DiffArgsParser() {
        super();
        legalFlags.add(FLAG_REP_IDENT);
        legalFlags.add(FLAG_IGN_BLANK);
        legalFlags.add(FLAG_MSG_DIFF);
    }

    public boolean isReportIdentical() { return flags.contains(FLAG_REP_IDENT); }

    public boolean isIgnoreBlanks() { return flags.contains(FLAG_IGN_BLANK); }

    public boolean isMsgDiff() { return flags.contains(FLAG_MSG_DIFF); }

    public List<String> getDiffFile() { return nonFlagArgs; }
}
