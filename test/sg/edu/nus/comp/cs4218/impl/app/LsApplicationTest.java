package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.EnvironmentUtils;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

class LsApplicationTest {

    private final LsInterface lsApplication = new LsApplication();
    private final OutputStream outputStream = new ByteArrayOutputStream();
    private final String cwd = EnvironmentUtils.currentDirectory;

    @BeforeEach
    void setCurrentDirectory() {
        //noinspection NonAtomicOperationOnVolatileField
        EnvironmentUtils.currentDirectory += CHAR_FILE_SEP + "asset" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "ls";
    }


    @AfterEach
    void resetCurrentDirectory() {
        EnvironmentUtils.currentDirectory = cwd;
    }

    @Test
    void runWithNullArgs() {
        assertThrows(LsException.class, ()->{
           lsApplication.run(null, System.in, outputStream);
        });
    }

    @Test
    void runWithNullOutputStream() {
        String[] args = {};
        assertThrows(LsException.class, ()->{
            lsApplication.run(args, System.in, null);
        });
    }

    @Test
    void runWithInvalidArgs() {
        String[] args = {"-a"};
        assertThrows(LsException.class, ()->{
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void runWithValidArgs() {
        String[] args = {"-R", "-d"};
        assertDoesNotThrow(()->{
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void runWithMultiArgs() {
        String[] args = {"test.txt", "subDir"};
        String result = "test.txt\n" +
                        "\n" +
                        "subDir:\n" +
                        "subSubDir\n";
        assertDoesNotThrow(()->{
            lsApplication.run(args, System.in, outputStream);
            assertEquals(result,outputStream.toString());

        });
    }


    @Test
    void runWithNonExistFile() {
        String[] args = {"test.txt", "subDir", "none.txt"};
        String result = "test.txt\n" +
                        "\n" +
                        "subDir:\n" +
                        "subSubDir\n" +
                        "\n" +
                        "ls: cannot access 'none.txt': No such file or directory\n";
        assertDoesNotThrow(()->{
            lsApplication.run(args, System.in, outputStream);
            assertEquals(result,outputStream.toString());
        });
    }

    @Test
    void runWithWrongOutputStream() {
        String[] args = {};
        try {
            try(OutputStream outputStreamTest = IOUtils.openOutputStream("asset/test.txt")) {
                IOUtils.closeOutputStream(outputStreamTest);
                assertThrows(LsException.class, () -> {
                    lsApplication.run(args, System.in, outputStreamTest);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ShellException e) {
            e.printStackTrace();
        }
    }

    @Test
    void listFolderContentWithEmptyFolderName() {
        String result = "subDir\n" +
                "subDir1\n" +
                "subDir2\n" +
                "test.txt\n" +
                "test1.txt\n" +
                "test2.txt";
        assertDoesNotThrow(()->{
            assertEquals(result,lsApplication.listFolderContent(false,false));
        });
        EnvironmentUtils.currentDirectory = cwd;
    }

    @Test
    void listFolderContentWithOnlyFolderFlag() {
        String result = "subDir:\nsubSubDir";
        assertDoesNotThrow(()->{
            assertEquals(result, lsApplication.listFolderContent(true, false, "subDir"));
        });
    }

    @Test
    void listFolderContentWithRecursionFlag() {
        String result = "";
        assertDoesNotThrow(()->{
            assertEquals(result, lsApplication.listFolderContent(false, true, "subDir"));
        });
    }

    @Test
    void listCwdContentWithWrongCwd() {
        //noinspection NonAtomicOperationOnVolatileField
        EnvironmentUtils.currentDirectory += "/none/";
        assertThrows(LsException.class, ()->{
            lsApplication.listFolderContent(false,false);
        });
        EnvironmentUtils.currentDirectory = cwd;
    }

}