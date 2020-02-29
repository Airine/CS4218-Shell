package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentUtils;
import sg.edu.nus.comp.cs4218.app.FindInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.FindException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class FindApplication implements FindInterface {
    public static final String FILE_IDENT = CHAR_FLAG_PREFIX + "name";
    public static final String NO_FOLDER = "No folder specified";
    public static final String NO_FILE = "No file specified";
    public static final String WRONG_FLAG_SUFFIX = "Flag must be " + FILE_IDENT;
    public static final String MULTIPLE_FILES = "Only one filename is allowed";
    public static final String PERMISSION_DENIED = "Permission Denied";
    public static final String NULL_POINTER = "Null Pointer Exception";
    public static final String EMPTY_ARG = "Arguments should not be empty";

    /**
     * Runs the find application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element can be a folder name, flag or filename.
     * @param stdin  An InputStream, not used.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     *
     * @throws FindException If the folder(s)/filename specified do not exist or are unreadable or
     * invalid arguments are given.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        ArrayList<String> folderNames = new ArrayList<>();

        try {
            if (args == null) {
                throw new Exception(NULL_POINTER);
            }

            String fileName = getArguments(args, folderNames);
            String[] folderNamesArray = new String[folderNames.size()];
            folderNamesArray = folderNames.toArray(folderNamesArray);
            String results = findFolderContent(fileName, folderNamesArray);
            String trimmedResults = results.trim();
            if (!trimmedResults.isEmpty()) {
                results += STRING_NEWLINE;
            }

            if (stdout == null) {
                throw new Exception(ERR_NO_OSTREAM);
            } else {
                stdout.write(results.getBytes());
            }

        } catch (FindException findException) {
            throw findException;
        } catch (Exception e) {
            throw new FindException(e, e.getMessage());
        }
    }

    /**
     * Get folder names and filename supplied by user.
     * @param args supplied by user.
     * @return a String of the regex of filename if specified by user, else returns an empty string
     */
    private String getArguments(String[] args, ArrayList<String> folderNames) throws Exception {
        String fileName = "";
        boolean isFileName = false;

        for (String s : args) {
            if (s.isEmpty()) {
                throw new FindException(EMPTY_ARG);
            }
            if (s.charAt(0) == CHAR_FLAG_PREFIX) {
                if (s.equals(FILE_IDENT)) { // next arg is filename
                    isFileName = true;
                    continue;
                } else {
                    throw new FindException(WRONG_FLAG_SUFFIX);
                }
            }

            if (isFileName) {
                if (fileName.isEmpty()) {
                    fileName = convertFileName(s);
                } else {
                    throw new FindException(MULTIPLE_FILES);
                }
            } else {
                folderNames.add(FileSystemUtils.convertPathToSystemPath(s));
            }
        }
        if (folderNames.isEmpty()) {
            throw new FindException(NO_FOLDER);
        } else if (fileName.isEmpty()) {
            throw new FindException(NO_FILE);
        }
        return fileName;
    }

    /**
     * Converts file name provided by user into regular expression format.
     * @param fileName supplied by user
     * @return a String the fileName in regular expression format
     */
    private String convertFileName(String fileName) {
        String convertedFileName = fileName;
        convertedFileName = convertedFileName.replaceAll("\\.", "\\\\.");
        convertedFileName = convertedFileName.replaceAll("\\*", ".*");
        convertedFileName = "^" + convertedFileName + "$";

        return convertedFileName;
    }

    @Override
    public String findFolderContent(String fileName, String... folderName) throws Exception {
        if (folderName == null || folderName.length == 0) {
            throw new FindException(NO_FOLDER);
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new FindException(NO_FILE);
        }

        String results = "";
        results = findInFolders(fileName, folderName);

        return results;
    }

    /**
     * Finds files/folders specified by the user. Folders will be displayed first before files, if in the same
     * nesting level; If folder is not empty, it will be recursively searched and its contents will be displayed
     * before other files in the same nesting level as the folder.
     * Returns all files and folders that matches the fileName specified.
     *
     * @param fileName   String of a regular expression of the file name
     * @param folderName Array of String of given folder/folders' name
     * @throws Exception
     * @return the string listing the names of the matched file/folder in the folders specified
     */
    private String findInFolders(String fileName, String... folderName) throws Exception {
        ArrayList<String> listOfFileNames;
        ArrayList<String> listOfFolderNames;
        String result = "";
        String tempResult;
        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);

        Pattern filePattern = Pattern.compile(fileName);

        for (String f : folderName) {
            String path = FileSystemUtils.convertToAbsolutePath(f);

            File folder = new File(path);

            if (!folder.exists() || !folder.isDirectory()) {
                stringJoiner.add("find: " + f + ": " + ERR_FILE_NOT_FOUND);
                continue;
            }
            if (!folder.canRead()) {
                throw new FindException(PERMISSION_DENIED);
            }

            if (f.lastIndexOf(CHAR_FILE_SEP) != -1) {
                String fName = f.substring(f.lastIndexOf(CHAR_FILE_SEP) + 1);
                Matcher matcher = filePattern.matcher(fName);
                if (matcher.find()) {
                    stringJoiner.add(f);
                }
            }

            listOfFileNames = getFilesFrom(folder);
            listOfFolderNames = getFoldersFrom(folder);

            String[] folderNamesArr = updatePath(listOfFolderNames, f);
            tempResult = findInFolders(fileName, folderNamesArr);
            if (!tempResult.isEmpty()) {
                stringJoiner.add(tempResult);
            }

            tempResult = formatResult(filePattern, listOfFileNames, f);
            if (!tempResult.isEmpty()) {
                stringJoiner.add(tempResult);
            }
        }
        result = stringJoiner.toString();

        return result;
    }

    /**
     * Sorts the listOfFileNames and listOfFolder Names in alphabetical order and format the files/folders
     * into the required format.
     * @param filePattern regex pattern of specified file, if isFindingFile is true
     * @param listOfFileNames unsorted list of file names
     * @param folderName subpath from pwd
     * @return string of all or specified file name(s) concatenated and delimited by a new line character
     */
    private String formatResult(Pattern filePattern, ArrayList<String> listOfFileNames, String folderName) {
        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);

        Collections.sort(listOfFileNames);

        for (String file : listOfFileNames) {
            Matcher matcher = filePattern.matcher(file);
            if (matcher.find()) {
                stringJoiner.add(folderName + CHAR_FILE_SEP + file);
            }
        }
        return stringJoiner.toString();
    }

    /**
     * Pre-condition: currFolder exists
     * @param currFolder the folder to list all files
     * @return an ArrayList<String> containing all the file names
     */
    private ArrayList<String> getFilesFrom(File currFolder) {
        File[] listOfFiles = currFolder.listFiles();
        ArrayList<String> listOfFileNames = new ArrayList<String>();

        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                listOfFileNames.add(listOfFile.getName().trim());
            }
        }
        Collections.sort(listOfFileNames);
        return listOfFileNames;
    }

    /**
     * Pre-condition: currFolder exists
     * @param currFolder the folder to list all folders
     * @return an ArrayList<String> containing all the folder names
     */
    private ArrayList<String> getFoldersFrom(File currFolder) {
        File[] listOfFiles = currFolder.listFiles();
        ArrayList<String> listOfFolderNames = new ArrayList<String>();

        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isDirectory()) {
                listOfFolderNames.add(listOfFile.getName().trim());
            }
        }
        Collections.sort(listOfFolderNames);
        return listOfFolderNames;
    }

    /**
     * Update folder names to include entire subpath from pwd
     * @param listOfFolderNames folder names to be updated
     * @param path subpath from pwd
     * @return String array containing updated paths
     */
    private String[] updatePath(ArrayList<String> listOfFolderNames, String path) {
        String[] folderNamesArr = new String[listOfFolderNames.size()];
        for (int i = 0; i < folderNamesArr.length; i++) {
            folderNamesArr[i] = path + CHAR_FILE_SEP + listOfFolderNames.get(i);
        }
        return folderNamesArr;
    }
}
