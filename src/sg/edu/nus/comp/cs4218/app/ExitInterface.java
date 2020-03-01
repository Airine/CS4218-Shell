package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.ExitException;

public interface ExitInterface extends Application {
    /**
     * Terminate shell.
     *
     * @throws ExitException The exception caused by ExitApplication
     */
    void terminateExecution() throws ExitException;
}
