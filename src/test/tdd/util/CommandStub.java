package tdd.util;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.EchoException;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CommandStub implements Command {

    public enum CommandT {
        ECHO_EXCEPTION,
        SHELL_EXCEPTION,
        EXIT_EXCEPTION,
        PRINT_TO_STDOUT,
        WHITESPACE;
    }

    private final CommandT commandType;

    public CommandStub(CommandT commandType) {
        this.commandType = commandType;
    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException, ShellException {

        switch (commandType) {
            case EXIT_EXCEPTION:
                throw new ExitException("ExitException");
            case SHELL_EXCEPTION:
                throw new ShellException("ShellException");
            case ECHO_EXCEPTION:
                throw new EchoException("EchoException");
            case PRINT_TO_STDOUT:
                try {
                    stdout.write("test ".getBytes());
                } catch (IOException e) {
                    //stub method so catch and do nothing
                }
                break;
            case WHITESPACE:
                try {
                    stdout.write(STRING_NEWLINE.getBytes());
                } catch (IOException e) {
                    //stub method so catch and do nothing
                }
                break;
            default:
                throw new ShellException("FAIL");
        }

    }

    @Override
    public void terminate() {
        // Unused for now
    }

}