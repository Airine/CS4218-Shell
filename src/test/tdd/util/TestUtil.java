package tdd.util;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("PMD.LongVariable")
public class TestUtil {

    public static Path resolveFilePath(String fileName) {
        Path currentDirectory = Paths.get(Environment.currentDirectory);
        return currentDirectory.resolve(fileName);
    }
}
