package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.DiffInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.DiffException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class DiffApplication implements DiffInterface {

    private void checkIfNullArgs(Object... args) throws DiffException {
        for (Object arg : args)
            if (arg == null) throw new DiffException(ERR_NULL_ARGS);
    }

    private void checkIfExistFiles(File... files) throws DiffException {
        for (File file : files)
            if (!file.exists()) throw new DiffException(ERR_FILE_NOT_FOUND);
    }

    private void checkIfValidFolder(File[] files) throws DiffException{
        if (files == null || files.length == 0)
            throw new DiffException(ERR_IS_DIR);
    }

    private List<String> getDiff(InputStream inputA, InputStream inputB) throws IOException {
        BufferedReader brA = new BufferedReader(new InputStreamReader(inputA));
        BufferedReader brB = new BufferedReader(new InputStreamReader(inputB));
        List<String> linesA = new ArrayList<>();
        List<String> linesB = new ArrayList<>();
        List<String> result = new ArrayList<>();
        String lineA, lineB;
        while((lineA = brA.readLine())!=null)
            linesA.add(lineA);
        while((lineB = brB.readLine())!=null)
            linesB.add(lineB);
        int lengthA = linesA.size();
        int lengthB = linesB.size();
        boolean[] commonA = new boolean[lengthA];
        boolean[] commonB = new boolean[lengthB];
        int[][] matrix = new int[lengthA+1][lengthB+1];
        for (int i = 0; i < lengthA; i++) {
            for (int j = 0; j < lengthB; j ++) {
                if (linesA.get(i).equals(linesB.get(j)))
                    matrix[i+1][j+1] = matrix[i][j] + 1;
                else
                    matrix[i+1][j+1] = Math.max(matrix[i][j+1], matrix[i+1][j]);
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
            if (!commonA[i])
                result.add("< "+linesA.get(i));
        }
        for (int i = 0; i < lengthB; i++) {
            if (!commonB[i])
                result.add("> "+linesB.get(i));
        }
        return result;
    }

    private List<String> getDiffDir(File[] filesA, File[] filesB) throws IOException {
        List<String> result = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < filesA.length && j < filesB.length) {

            i++;
            j++;
        }
        return result;
    }

    @Override
    public String diffTwoFiles(String fileNameA, String fileNameB, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        checkIfNullArgs(fileNameA, fileNameB);
        File fileA = IOUtils.resolveFilePath(fileNameA).toFile();
        File fileB = IOUtils.resolveFilePath(fileNameB).toFile();
        checkIfExistFiles(fileA, fileB);
        List<String> result = new ArrayList<>();
        try {
            InputStream inputStreamA = IOUtils.openInputStream(fileA);
            InputStream inputB = IOUtils.openInputStream(fileB);
            result = getDiff(inputStreamA, inputB);
        } catch (ShellException | IOException e) {
            throw new DiffException(e.getMessage());
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


        return null;
    }

    @Override
    public String diffFileAndStdin(String fileName, InputStream stdin, Boolean isShowSame, Boolean isNoBlank, Boolean isSimple) throws DiffException {
        checkIfNullArgs(fileName, stdin);
        File file = IOUtils.resolveFilePath(fileName).toFile();
        checkIfExistFiles(file);
        List<String> result = new ArrayList<>();
        try {
            InputStream inputStreamA = IOUtils.openInputStream(file);
            result = getDiff(inputStreamA, stdin);
        } catch (ShellException | IOException e) {
            e.printStackTrace();
        }
        return String.join(STRING_NEWLINE, result);
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

    }
}
