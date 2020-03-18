package ef1;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.CdInterface;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.app.CdApplication;
import sg.edu.nus.comp.cs4218.impl.app.TestFileUtils;

import static org.junit.jupiter.api.Assertions.*;

class CdApplicationTest {

    private final static String NOT_EXIST = "Not_exist";
    CdInterface cdInterface = new CdApplication();
    private String currDir;

    @BeforeAll
    static void setUp() {
        try {
            TestFileUtils.createSomeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown() {
        TestFileUtils.rmCreatedFiles();
    }


    @BeforeEach
    public void saveCurrDir() {
        currDir = Environment.currentDirectory;
    }

    @AfterEach
    public void resumeCurrDir() {
        Environment.currentDirectory = currDir;
    }

    @Test
    public void testCdEmpty() {
        assertThrows(CdException.class,
                () -> cdInterface.changeToDirectory(""));
    }

    @Test
    public void testCdNotExist() {
        assertThrows(CdException.class,
                () -> cdInterface.changeToDirectory(NOT_EXIST));
    }

    @Test
    public void testCdFile() {
        assertThrows(CdException.class,
                () -> cdInterface.changeToDirectory(TestFileUtils.tempFileName1));

    }

    @Test
    public void testCdFolder() {
        assertDoesNotThrow(() -> cdInterface.changeToDirectory(TestFileUtils.tempFolderName));
        assertEquals(TestFileUtils.tempFolderName, Environment.currentDirectory);
    }

    @Test
    public void runCdFolder() {
        assertDoesNotThrow(() -> cdInterface.run(new String[]{TestFileUtils.tempFolderName}, System.in, System.out));
        assertEquals(TestFileUtils.tempFolderName, Environment.currentDirectory);
    }

    @Test
    public void runCdNull() {
        assertThrows(CdException.class, () -> cdInterface.run(null, System.in, System.out));
    }


}