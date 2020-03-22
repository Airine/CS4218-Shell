package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.parser.DiffArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class DiffApplication implements DiffInterface {//NOPMD


    /**
     * Runs the Diff application.
     *
     * @param args   Array of arguments for the application
     * @param stdin  An InputStream
     * @param stdout An OutputStream
     * @throws AbstractApplicationException An DiffException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        checkIfNullArgs(args, stdin, stdout);
        DiffArgsParser parser = new DiffArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new DiffException(e.getMessage());
        }

        Boolean isShowSame = parser.isReportIdentical();
        Boolean isNoBlank = parser.isIgnoreBlanks();
        Boolean isSimple = parser.isMsgDiff();

        String[] files = parser.getDiffFile().toArray(new String[0]);

        if (files.length > 2) {
            throw new DiffException(ERR_TOO_MANY_ARGS);
        } else if (files.length < 2) {
            throw new DiffException(ERR_NO_ARGS);
        }

        String nameA = files[0];
        String nameB = files[1];
        String result = "";
        if ("-".equals(nameA))  {
            if ("-".equals(nameB)) {
                throw new DiffException("Can not diff between stdin with stdin");
            } else {
                result = diffFileAndStdin(nameB, stdin, isShowSame, isNoBlank, isSimple);
            }
        } else if ("-".equals(nameB)) {
            result = diffFileAndStdin(nameA, stdin, isShowSame, isNoBlank, isSimple);
        } else {
            result = diffTwoString(nameA, nameB, isShowSame, isNoBlank, isSimple);
        }

        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (Exception e) {
            throw new DiffException(e.getMessage());
        }
    }

    /**
     * Run diff between two fileNames
     * @param fileNameA file name of fileA, may be file or directory
     * @param fileNameB file name of fileB, may be file or directory
     * @param isShowSame the s flag
     * @param isNoBlank the B flag
     * @param isSimple the q flag
     * @return the expected output result
     * @throws DiffException the DiffException
     */
    private String diffTwoString(String fileNameA, String fileNameB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        File fileA = IOUtils.resolveFilePath(fileNameA).toFile();
        File fileB = IOUtils.resolveFilePath(fileNameB).toFile();
        if (fileA.isDirectory() && fileB.isDirectory()) {
            return diffTwoDir(fileNameA, fileNameB, isShowSame, isNoBlank, isSimple);
        } else if (fileA.isFile() && fileB.isFile()) {
            return diffTwoFiles(fileNameA, fileNameB, isShowSame, isNoBlank, isSimple);
        } else {
            throw new DiffException("Can not diff between a file and a directory");
        }
    }

    private void checkIfNullArgs(Object... args) throws DiffException {
        for (Object arg : args) {
            if (arg == null) {throw new DiffException(ERR_NULL_ARGS);}
        }
    }

    private void checkIfExistFiles(File... files) throws DiffException {
        for (File file : files) {
            if (!file.exists()) {throw new DiffException(ERR_FILE_NOT_FOUND);}
        }
    }

    private void checkIfValidFolder(File[] files) throws DiffException{
        if (files == null) {
            throw new DiffException(ERR_IS_DIR);
        }
    }

    /**
     * Get List of result from two InputStream
     * @param inputA
     * @param inputB
     * @param isShowSame
     * @param isNoBlank
     * @param isSimple
     * @return
     * @throws IOException
     */
    private List<String> getDiff(InputStream inputA, InputStream inputB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws IOException {
        BufferedReader brA = new BufferedReader(new InputStreamReader(inputA));
        BufferedReader brB = new BufferedReader(new InputStreamReader(inputB));
        List<String> linesA = new ArrayList<>();
        List<String> linesB = new ArrayList<>();
        List<String> result = new ArrayList<>();
        String lineA, lineB;
        while((lineA = brA.readLine())!=null) {
            if (isNoBlank && StringUtils.isBlank(lineA)) {
                continue;
            }
            linesA.add(lineA);
        }
        while((lineB = brB.readLine())!=null){
            if (isNoBlank && StringUtils.isBlank(lineB)) {
                continue;
            }
            linesB.add(lineB);
        }
        int lengthA = linesA.size();
        int lengthB = linesB.size();
        boolean[] commonA = new boolean[lengthA];
        boolean[] commonB = new boolean[lengthB];
        int[][] matrix = new int[lengthA+1][lengthB+1];
        for (int i = 0; i < lengthA; i++) {
            for (int j = 0; j < lengthB; j ++) {
                if (linesA.get(i).equals(linesB.get(j))) {
                    matrix[i + 1][j + 1] = matrix[i][j] + 1;
                } else {
                    matrix[i + 1][j + 1] = Math.max(matrix[i][j + 1], matrix[i + 1][j]);
                }
            }
        }
        int current = 1;
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                if (matrix[i][j] == current && linesA.get(i-1).equals(linesB.get(j-1))) {
                    commonA[i-1] = true;
                    commonB[j-1] = true;
                    current += 1;
                }
            }
        }
        for (int i = 0; i < lengthA; i++) {
            if (!commonA[i]) {
                result.add("< " + linesA.get(i));
            }
        }
        for (int i = 0; i < lengthB; i++) {
            if (!commonB[i]) {
                result.add("> " + linesB.get(i));
            }
        }
        return result;
    }

    private List<String> getDiffDir(File[] filesA, File[] filesB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        List<String> result = new ArrayList<>();
        int i = 0;
        int j = 0;
        File fileA;
        File fileB;
        String sep = File.separator;
        String nameA, nameB;
        String absA, absB;
        String dirA, dirB;
        int idxA, idxB;
        int idx2A, idx2B;
        while (i < filesA.length && j < filesB.length) {
            fileA = filesA[i];
            fileB = filesB[j];

            absA = fileA.getAbsolutePath();
            idxA = absA.lastIndexOf(sep);
            nameA = absA.substring(idxA+1);
            idx2A = absA.substring(0,idxA).lastIndexOf(sep);
            dirA = absA.substring(idx2A+1, idxA);

            absB = fileB.getAbsolutePath();
            idxB = absB.lastIndexOf(sep);
            nameB = absB.substring(idxB+1);
            idx2B = absB.substring(0,idxB).lastIndexOf(sep);
            dirB = absB.substring(idx2B+1, idxB);

            if (nameA.equals(nameB)){

                try (InputStream inputStreamA = IOUtils.openInputStream(fileA);
                     InputStream inputStreamB = IOUtils.openInputStream(fileB)) {
                    List<String> tmp = getDiff(inputStreamA, inputStreamB, isShowSame, isNoBlank, isSimple);
                    if (tmp.size() != 0) {
                        if (isSimple) {
                            result.add("Files " + dirA + sep + nameA + " " + dirB + sep + nameB + " differ");
                        } else {
                            result.add("diff " + dirA + sep + nameA + " " + dirB + sep + nameB);
                            result.addAll(tmp);
                        }
                    } else {
                        if (isShowSame) {
                            result.add("Files " + dirA + sep + nameA + " " + dirB + sep + nameB + " are identical");
                        }
                    }
                } catch (ShellException | IOException e) {
                    throw new DiffException(e.getMessage());
                }
                i++;
                j++;
            } else if (nameA.compareTo(nameB) > 0) {
                j++;
                result.add("Only in " + dirB + ": " + nameB);
            } else {
                result.add("Only in " + dirA + ": " + nameA);
                i++;
            }
        }
        if (i == filesA.length && j < filesB.length) {
            getRemain(filesB, result, j, sep);
        } else if (i < filesA.length && j == filesB.length) {
            getRemain(filesA, result, i, sep);
        }

        return result;
    }

    private void getRemain(File[] filesB, List<String> result, int j, String sep) {
        File fileB;
        String absB;
        int idxB;
        String nameB;
        int idx2B;
        String dirB;
        for (; j < filesB.length; j++){
            fileB = filesB[j];
            absB = fileB.getAbsolutePath();
            idxB = absB.lastIndexOf(sep);
            nameB = absB.substring(idxB+1);
            idx2B = absB.substring(0,idxB).lastIndexOf(sep);
            dirB = absB.substring(idx2B+1, idxB);
            result.add("Only in " + dirB + ": " + nameB);
        }
    }

    @Override
    public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        checkIfNullArgs(fileNameA, fileNameB);
        File fileA = IOUtils.resolveFilePath(fileNameA).toFile();
        File fileB = IOUtils.resolveFilePath(fileNameB).toFile();
        checkIfExistFiles(fileA, fileB);
        List<String> result = new ArrayList<>();
        try (InputStream inputStreamA = IOUtils.openInputStream(fileA);
             InputStream inputStreamB = IOUtils.openInputStream(fileB)) {
            List<String> tmp = getDiff(inputStreamA, inputStreamB, isShowSame, isNoBlank, isSimple);
            if (tmp.size() > 0) {
                if (isSimple) {
                    result.add("Files " + fileNameA + " " + fileNameB + " differ");
                } else {
                    result.addAll(tmp);
                }
            }
        } catch (ShellException | IOException e) {
            throw new DiffException(e.getMessage());
        }
        if (isShowSame && result.size() == 0) {
            result.add("Files " + fileNameA + " " + fileNameB + " are identical");
        }
        return String.join(STRING_NEWLINE, result);
    }

    @Override
    public String diffTwoDir(String folderA, String folderB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        checkIfNullArgs(folderA, folderB);
        File folderAA = IOUtils.resolveFilePath(folderA).toFile();
        File folderBB = IOUtils.resolveFilePath(folderB).toFile();
        checkIfExistFiles(folderAA, folderBB);
        checkIfValidFolder(folderAA.listFiles());
        checkIfValidFolder(folderBB.listFiles());
        File[] filesA = Stream.of(Objects.requireNonNull(folderAA.listFiles())).sorted(Comparator.comparing(File::toString)).toArray(File[]::new);
        File[] filesB = Stream.of(Objects.requireNonNull(folderBB.listFiles())).sorted(Comparator.comparing(File::toString)).toArray(File[]::new);
        List<String> result;
        result = getDiffDir(filesA, filesB, isShowSame, isNoBlank, isSimple);
        return String.join(STRING_NEWLINE, result);
    }

    @Override
    public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        checkIfNullArgs(fileName, stdin);
        File file = IOUtils.resolveFilePath(fileName).toFile();
        checkIfExistFiles(file);
        List<String> result = new ArrayList<>();
        try (InputStream inputStreamA = IOUtils.openInputStream(file)) {
            List<String> tmp = getDiff(inputStreamA, stdin, isShowSame, isNoBlank, isSimple);
            if (tmp.size() > 0) {
                if (isSimple) {
                    result.add("Files " +fileName+" - differ");
                } else {
                    result.addAll(tmp);
                }
            }
        } catch (ShellException | IOException e) {
            e.printStackTrace();
        }
        if (isShowSame && result.size() == 0) {
            result.add("Files " +fileName+" - are identical");
        }
        return String.join(STRING_NEWLINE, result);
    }

}
