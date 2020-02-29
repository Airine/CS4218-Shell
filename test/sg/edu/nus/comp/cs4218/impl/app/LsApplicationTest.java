package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentUtils;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class LsApplicationTest {

    private final LsInterface lsApplication = new LsApplication();
    private final static String TEMPT_TXT ="tempt.txt";
    private final OutputStream outputStream = new ByteArrayOutputStream();

    @BeforeAll
    static void setUp(){
        try {
            FileSystemUtils.deleteFileRecursive(new File(TEMPT_TXT));
            FileSystemUtils.createFile(TEMPT_TXT);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String[] args = {"README.md", "img"};
        String result = "README.md\n" +
                        "\n" +
                        "img:\n" +
                        "timeline.png\n";
        assertDoesNotThrow(()->{
            lsApplication.run(args, System.in, outputStream);
            assertEquals(result,outputStream.toString());

        });
    }


    @Test
    void runWithNonExistFile() {
        String[] args = {"README.md", "img", "none.txt"};
        String result = "README.md\n" +
                "\n" +
                "img:\n" +
                "timeline.png\n" +
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
            try(OutputStream outputStreamTest = IOUtils.openOutputStream(TEMPT_TXT)) {
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
        String result = "LOG.md\n" +
                "PMD.Rules.xml\n" +
                "README.md\n" +
                "asset\n" +
                "img\n" +
                "pom.xml\n" +
                "results\n" +
                "src\n" +
                "target\n" +
                "tempt.txt\n" +
                "test";
        assertDoesNotThrow(()->{
            assertEquals(result,lsApplication.listFolderContent(false,false));
        });
    }

    @Test
    void listFolderContentWithOnlyFolderFlag() {
        String result = "asset\n" +
                "img\n" +
                "results\n" +
                "src\n" +
                "target\n" +
                "test";
        assertDoesNotThrow(()->{
            assertEquals(result, lsApplication.listFolderContent(true, false));
        });
    }

    @Test
    void listFolderContentWithRecursionFlag() {
        String result = "asset:\n" +
                "A.txt\n" +
                "B.txt\n" +
                "C.txt\n" +
                "D.txt\n" +
                "E.txt\n" +
                "empty1.txt\n" +
                "empty2.txt\n" +
                "subDir\n" +
                "\n" +
                "asset/subDir:\n" +
                "empty3.txt";
        assertDoesNotThrow(()->{
            assertEquals(result, lsApplication.listFolderContent(false, true, "asset"));
        });
    }

    @Test
    void listCwdContentWithWrongCwd() {
        String cwd = EnvironmentUtils.currentDirectory;
        //noinspection NonAtomicOperationOnVolatileField
        EnvironmentUtils.currentDirectory += "/none/";
        assertThrows(LsException.class, ()->{
            lsApplication.listFolderContent(false,false);
        });
        EnvironmentUtils.currentDirectory = cwd;
    }

    @AfterAll
    static void tearDown() {
        FileSystemUtils.deleteFileRecursive(new File(TEMPT_TXT));
    }
}