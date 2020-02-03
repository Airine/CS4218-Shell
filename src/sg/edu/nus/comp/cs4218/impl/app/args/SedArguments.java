package sg.edu.nus.comp.cs4218.impl.app.args;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class SedArguments {

    private final List<String> files;
    private String regex, replacement;
    private int replacementIndex;

    public SedArguments() {
        this.regex = null;
        this.replacement = null;
        this.files = new ArrayList<>();
    }

    public static void validate(String regexp, String replacement, int replacementIndex) throws Exception {
        if (regexp == null || replacement == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        if (replacementIndex < 1) {
            throw new Exception(ERR_INVALID_REP_X);
        }
        if (regexp.isEmpty()) {
            throw new Exception(ERR_EMPTY_REGEX);
        }
        try {
            Pattern.compile(regexp); // Test if valid regex
        } catch (PatternSyntaxException e) {
            throw new Exception(ERR_INVALID_REGEX);//NOPMD
        }
    }

    /**
     * Handles argument list parsing for the `sed` application.
     *
     * @param args Array of arguments to parse
     * @throws Exception
     */
    public void parse(String... args) throws Exception {
        if (args == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        if (args.length < 1) {
            throw new Exception(ERR_NO_REP_RULE);
        }

        // Parse replacement rule
        if (args[0].length() < 4) { // s/// are the minimum characters
            throw new Exception(ERR_INVALID_REP_RULE);
        }
        char delimiter = args[0].charAt(1);
        int index1 = args[0].indexOf(String.valueOf(delimiter), 2);
        int index2 = args[0].indexOf(String.valueOf(delimiter), index1 + 1);
        if (args[0].charAt(0) != 's' || index1 < 0 || index2 < 0) {
            throw new Exception(ERR_INVALID_REP_RULE);
        }
        this.regex = args[0].substring(2, index1);
        this.replacement = args[0].substring(index1 + 1, index2);
        String remaining = args[0].substring(index2 + 1);
        try {
            this.replacementIndex = Integer.parseInt(remaining.isEmpty() ? "1" : remaining);
        } catch (NumberFormatException e) {
            throw new Exception(ERR_INVALID_REP_X);//NOPMD
        }

        // Parse files
        for (int index = 1; index < args.length; ++index) {
            this.files.add(args[index].trim());
        }

        validate(this.regex, this.replacement, this.replacementIndex);
    }

    public String getRegex() {
        return regex;
    }

    public String getReplacement() {
        return replacement;
    }

    public List<String> getFiles() {
        return files;
    }

    public int getReplacementIndex() {
        return replacementIndex;
    }
}
