package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.ExitInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.security.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ExitApplicationTest {

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
        } catch (TestExitException e) {
            assertEquals(0, e.status, "Exit status");
        } catch (AbstractApplicationException e) {
            fail(e);
        }
    }

    protected static class TestExitException extends SecurityException {
        final int status;

        TestExitException(int status) {
            super("There is no escape!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            //TODO
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            //TODO
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new TestExitException(status);
        }
    }

}