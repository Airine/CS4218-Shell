package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class LsApplicationTest {

    private final LsInterface lsApplication = new LsApplication();
    private final static String TEMPT_TXT ="tempt.txt";
    private OutputStream outputStream = new ByteArrayOutputStream();

    @BeforeAll
    static void setUp(){
        try {
            FileSystemUtils.deleteFileRecursive(new File(TEMPT_TXT));
            FileSystemUtils.createTestFile(TEMPT_TXT);
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
    void runWithWrongOutputStream() {
        String[] args = {};
        try {
            OutputStream outputStreamTest = IOUtils.openOutputStream(TEMPT_TXT);
            IOUtils.closeOutputStream(outputStreamTest);
            assertThrows(LsException.class, ()->{
                lsApplication.run(args, System.in, outputStreamTest);
            });
        } catch (ShellException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown() {
        FileSystemUtils.deleteFileRecursive(new File(TEMPT_TXT));
    }
}