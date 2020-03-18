package tdd.bf;

import static org.junit.jupiter.api.Assertions.*;

import java.security.Permission;

import org.junit.jupiter.api.*;

import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.impl.app.ExitApplication;

class ExitApplicationTest {
    private ExitApplication exitApp;

    protected static class TestExitException extends SecurityException {
        public final int status;
        public TestExitException(int status) {
            super("Expected Exit");
            this.status = status;
        }
    }
    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm)
        {
            // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context)
        {
            // allow anything.
        }
        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new TestExitException(status);

        }
    }

    @BeforeAll
    static void setUp() {
        System.out.println("Starting Exit App Test");
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @BeforeEach
    public void initialisation() {
        exitApp = new ExitApplication();
    }

    @Test
    public void testExit(){
        try {
            exitApp.terminateExecution();
        } catch (TestExitException e) {
            assertEquals(0, e.status);
        } catch (ExitException e) {
            fail("Expected Exit");
        }
    }

    @AfterEach
    public void tearDown() {
        System.setSecurityManager(null);
    }
}