package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.util.Iterator;

public class RmArgsParser extends ArgsParser {
    private static final char RECURSIVE_FLAG = 'r';
    private static final char EMPTY_FOLDER_FLAG = 'd';

    public RmArgsParser() {
        super();
        this.legalFlags.add(RECURSIVE_FLAG);
        this.legalFlags.add(EMPTY_FOLDER_FLAG);
    }

    public boolean isRecursive() {
        return super.flags.contains(RECURSIVE_FLAG);
    }

    public boolean isEmptyFolder() {
        return super.flags.contains(EMPTY_FOLDER_FLAG);
    }

    public String[] files() {

        String[] strings = new String[super.nonFlagArgs.size()];
        Iterator<String> iterator = super.nonFlagArgs.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            strings[index] = FileSystemUtils.getAbsolutePathName(iterator.next());
            index++;
        }
        return strings;
    }

}
