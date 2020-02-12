package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import sg.edu.nus.comp.cs4218.app.ExitInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.security.Permission;

class ExitApplicationTest {

    protected static class testExitException extends SecurityException {
        final int status;
        testExitException(int status) {
            super("There is no escape!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {}

        @Override
        public void checkPermission(Permission perm, Object context) {}

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new testExitException(status);
        }
    }

    private final ExitInterface exitApp = new ExitApplication();
    private final String[] args = {};

    @BeforeAll
    static void setUp() {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @AfterAll
    static void tearDown() {
        System.setSecurityManager(null);
    }

    @Test
    void testExit() {
        try {
            exitApp.run(args, System.in, System.out);
        } catch (testExitException e){
            assertEquals(0, e.status, "Exit status");
        } catch (AbstractApplicationException e) {
            fail(e);
        }
    }

}