package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.FindException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_CURR_DIR;

public class FindArguments {

    private final Set<String> directories;
    private String filename;

    public FindArguments() {
        this.filename = null;
        this.directories = new HashSet<>();
    }

    /**
     * Handles argument list parsing for the `find` application.
     *
     * @param args Array of arguments to parse
     * @throws FindException
     */
    public void parse(String... args) throws FindException {
        if (args == null) {
            throw new FindException(ERR_NULL_ARGS);
        }
        int index = 0;
        while (index < args.length) {
            if (this.filename != null) { // Already parsed filename
                throw new FindException(ERR_NAME_FLAG); // -name flag before folder
            }
            if (args[index].length() == 0) {
                throw new FindException(ERR_MISSING_ARG);
            }
            if (args[index].equals(CHAR_FLAG_PREFIX + "name") && index + 1 < args.length) {
                if (args[index + 1].indexOf(File.separator) > -1) { // Filename cannot have slashes
                    throw new FindException(ERR_INVALID_FILE);
                }
                this.filename = args[index + 1];
                index += 2;
            } else {
                this.directories.add(args[index].trim());
                ++index;
            }
        }
        if (this.directories.isEmpty()) {
            this.directories.add(STRING_CURR_DIR);
        }
    }

    public String getFilename() {
        return filename;
    }

    public Set<String> getDirectories() {
        return directories;
    }
}
