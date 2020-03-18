package tdd.util;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CallCommandStub extends CallCommand {

    public enum CallCommandT {
        FIRST_COMMAND,
        SECOND_COMMAND,
        FIRST_INPUT_THROW_EXCEPTION,
        SECOND_INPUT_THROW_EXCEPTION,
        FIRST_OUTPUT;
    }

    private final CallCommandT commandType;

    public CallCommandStub(List<String> argsList, ApplicationRunner appRunner, ArgumentResolver argumentResolver, CallCommandT commandType) {
        super(argsList, appRunner, argumentResolver);
        this.commandType = commandType;
    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException, ShellException {

        switch(commandType){
            case FIRST_COMMAND:
                try {
                    stdout.write(CallCommandT.FIRST_OUTPUT.toString().getBytes());
                }catch (IOException ignored){

                }
                break;
            case SECOND_COMMAND:
                try{
                    if ((IOUtils.getLinesFromInputStream(stdin).get(0)).equals(CallCommandT.FIRST_OUTPUT.toString())) {
                        stdout.write("test".getBytes());
                    }
                }catch (Exception ignored){

                }
                break;
            case FIRST_INPUT_THROW_EXCEPTION:
                throw new ShellException("First Input ShellException");
            case SECOND_INPUT_THROW_EXCEPTION:
                throw new ShellException("Second Input ShellException");
            default:
                throw new ShellException("Fail");
        }
    }

    @Override
    public void terminate() {
        // Unused for now
    }

}