package sg.edu.nus.comp.cs4218.impl.cmd;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.List;

/**
 * A Pipe Command is a sub-command consisting of two Call Commands separated with a pipe,
 * or a Pipe Command and a Call Command separated with a pipe.
 * <p>
 * Command format: <Call> | <Call> or <Pipe> | <Call>
 */
public class PipeCommand implements Command {
    private final List<CallCommand> callCommands;

    public PipeCommand(List<CallCommand> callCommands) {
        this.callCommands = callCommands;
    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException, ShellException {
        AbstractApplicationException absAppException = null;
        ShellException shellException = null;

        InputStream nextInputStream = stdin;//NOPMD avoid close std in by using IOUtils.closeInputStream()
        OutputStream nextOutputStream = stdout;//NOPMD avoid close std out in by using IOUtils.closeOutputStream()

        for (int i = 0; i < callCommands.size(); i++) {
            CallCommand callCommand = callCommands.get(i);

            if (absAppException != null || shellException != null) {
                callCommand.terminate();
                continue;
            }

            try {
                if (i < callCommands.size() - 1) {
                    nextOutputStream = new ByteArrayOutputStream();
                } else {
                    nextOutputStream = stdout;
                }

                callCommand.evaluate(nextInputStream, nextOutputStream);
                if (i < callCommands.size() - 1) {
                    nextInputStream = new ByteArrayInputStream(((ByteArrayOutputStream) nextOutputStream).toByteArray());
                }

            } catch (AbstractApplicationException e) {
                absAppException = e;
            } catch (ShellException e) {
                shellException = e;
            } finally {
                IOUtils.closeInputStream(nextInputStream);
                IOUtils.closeOutputStream(nextOutputStream);
            }
        }

        if (absAppException != null) {
            throw absAppException;
        }
        if (shellException != null) {
            throw shellException;
        }

    }

    @Override
    public void terminate() {
        // do not need to implement
    }

    public List<CallCommand> getCallCommands() {
        return callCommands;
    }
}
