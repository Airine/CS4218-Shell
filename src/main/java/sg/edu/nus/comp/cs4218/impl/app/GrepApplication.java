package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplication implements GrepInterface {
    public static final String INVALID_PATTERN = "Invalid pattern syntax";
    public static final String EMPTY_PATTERN = "Pattern should not be empty.";
    public static final String IS_DIRECTORY = "This is a directory";
    public static final String NULL_POINTER = "Null Pointer Exception";

    private static final int NUM_ARGUMENTS = 2;
    private static final char CASE_INSEN_IDENT = 'i';
    private static final char COUNT_IDENT = 'c';
    private static final int CASE_INSEN_IDX = 0;
    private static final int COUNT_INDEX = 1;

    @Override
    public String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, String... fileNames) throws Exception {
        if (fileNames == null || pattern == null) {
            throw new GrepException(NULL_POINTER);
        }
        if (isCaseInsensitive == null){
            throw new GrepException(NULL_POINTER);
        }
        if (isCountLines == null){
            throw new GrepException(NULL_POINTER);
        }

        StringJoiner lineResults = new StringJoiner(STRING_NEWLINE);
        StringJoiner countResults = new StringJoiner(STRING_NEWLINE);

        grepResultsFromFiles(pattern, isCaseInsensitive, lineResults, countResults, fileNames);

        String results = "";
        if (isCountLines) {
            results = countResults.toString() + STRING_NEWLINE;
        } else {
            if (!lineResults.toString().isEmpty()) {
                results = lineResults.toString() + STRING_NEWLINE;
            }
        }
        return results;
    }

    /**
     * Extract the lines and count number of lines for grep from files and insert them into
     * lineResults and countResults respectively.
     *
     * @param pattern           supplied by user
     * @param isCaseInsensitive supplied by user
     * @param lineResults       a StringJoiner of the grep line results
     * @param countResults      a StringJoiner of the grep line count results
     * @param fileNames         a String Array of file names supplied by user
     */
    private void grepResultsFromFiles(String pattern, Boolean isCaseInsensitive, StringJoiner lineResults, StringJoiner countResults, String... fileNames) throws Exception {//NOPMD
//       todo this method is too long, try to split it in future
        int count;
        boolean isSingleFile = (fileNames.length == 1);
        for (String f : fileNames) {
            BufferedReader reader = null;
            try {
                if ("".equals(f) || f.charAt(0) == ' '){
                    lineResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    countResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    continue;
                }
                String path = FileSystemUtils.getAbsolutePathName(f);
                File file = new File(path);
                if (!file.exists()) {
                    lineResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    countResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    continue;
                }
                if (file.isDirectory()) { // ignore if it's a directory
                    lineResults.add(f + ": " + IS_DIRECTORY);
                    countResults.add(f + ": " + IS_DIRECTORY);
                    continue;
                }
                if (!file.canRead()){
                    lineResults.add(f + ": " + ERR_NO_PERM);
                    countResults.add(f + ": " + ERR_NO_PERM);
                    continue;
                }
                reader = new BufferedReader(new FileReader(path));
                String line;
                Pattern compiledPattern;
                if (isCaseInsensitive) {
                    compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                } else {
                    compiledPattern = Pattern.compile(pattern);
                }
                count = 0;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = compiledPattern.matcher(line);
                    if (matcher.find()) { // match
                        if (isSingleFile) {
                            lineResults.add(line);
                        } else {
                            lineResults.add(f + ": " + line);
                        }
                        count++;
                    }
                }
                if (isSingleFile) {
                    countResults.add("" + count);
                } else {
                    countResults.add(f + ": " + count);
                }
                reader.close();
            } catch (PatternSyntaxException pse) {
                throw (GrepException) new GrepException(ERR_INVALID_REGEX).initCause(pse);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    @Override
    public String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, InputStream stdin) throws Exception {
        int count = 0;
        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);

        if (isCaseInsensitive == null){
            throw new GrepException(NULL_POINTER);
        }
        if (isCountLines == null){
            throw new GrepException(NULL_POINTER);
        }
        if (pattern ==  null){
            throw new GrepException(NULL_POINTER);
        }
        if (stdin == null){
            throw new GrepException(NULL_POINTER);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stdin));
            String line;
            Pattern compiledPattern;
            if (isCaseInsensitive) {
                compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            } else {
                compiledPattern = Pattern.compile(pattern);
            }
            while ((line = reader.readLine()) != null) {
                Matcher matcher = compiledPattern.matcher(line);
                if (matcher.find()) { // match
                    stringJoiner.add(line);
                    count++;
                }
            }
        } catch (PatternSyntaxException pse) {
            throw (GrepException) new GrepException(ERR_INVALID_REGEX).initCause(pse);
        } catch (NullPointerException npe) {
            throw (GrepException) new GrepException(ERR_FILE_NOT_FOUND).initCause(npe);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        String results = "";
        if (isCountLines) {
            results = count + STRING_NEWLINE;
        } else {
            if (!stringJoiner.toString().isEmpty()) {
                results = stringJoiner.toString() + STRING_NEWLINE;
            }
        }
        return results;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            boolean[] grepFlags = new boolean[NUM_ARGUMENTS];
            ArrayList<String> inputFiles = new ArrayList<>();
            String pattern = getGrepArguments(args, grepFlags, inputFiles);
            String result = "";

            if (stdin == null && inputFiles.isEmpty()) {
                throw new Exception(ERR_NO_INPUT);
            }
            if (pattern == null) {
                throw new Exception(ERR_SYNTAX);
            }

            if (pattern.isEmpty()) {
                throw new Exception(EMPTY_PATTERN);
            } else {
                if (inputFiles.isEmpty()) {
                    result = grepFromStdin(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], stdin);
                } else {
                    String[] inputFilesArray = new String[inputFiles.size()];
                    inputFilesArray = inputFiles.toArray(inputFilesArray);
                    result = grepFromFiles(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], inputFilesArray);

                }
            }
            stdout.write(result.getBytes());
        } catch (GrepException grepException) {
            throw grepException;
        } catch (Exception e) {
            throw (GrepException) new GrepException(e.getMessage()).initCause(e);
        }
    }

    /**
     * Separates the arguments provided by user into the flags, pattern and input files.
     *
     * @param args       supplied by user
     * @param grepFlags  a bool array of possible flags in grep
     * @param inputFiles a ArrayList<String> of file names supplied by user
     * @return regex pattern supplied by user. An empty String if not supplied.
     */
    private String getGrepArguments(String[] args, boolean[] grepFlags, ArrayList<String> inputFiles) throws Exception {
        String pattern = null;
        boolean isFile = false; // files can only appear after pattern

        for (String s : args) {
            char[] arg = s.toCharArray();
            if (isFile) {
                inputFiles.add(s);
            } else {
                if (!s.isEmpty() && arg[0] == CHAR_FLAG_PREFIX) {
                    arg = Arrays.copyOfRange(arg, 1, arg.length);
                    for (char c : arg) {
                        switch (c) {
                            case CASE_INSEN_IDENT:
                                grepFlags[CASE_INSEN_IDX] = true;
                                break;
                            case COUNT_IDENT:
                                grepFlags[COUNT_INDEX] = true;
                                break;
                            default:
                                throw new GrepException(ERR_SYNTAX);
                        }
                    }
                } else { // pattern must come before file names
                    pattern = s;
                    isFile = true; // next arg onwards will be file
                }
            }
        }
        return pattern;
    }
}
