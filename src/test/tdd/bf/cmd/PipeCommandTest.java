package tdd.bf.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import tdd.util.CallCommandStub;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PipeCommandTest {

    private InputStream inputStream;
    private OutputStream outputStream;
    private List<CallCommand> callCommand;
    private List<String> argsList;
    private ApplicationRunner appRunner;
    private ArgumentResolver argumentResolver;

    @BeforeEach
    void setUp() {
        inputStream = new ByteArrayInputStream("".getBytes());
        outputStream = new ByteArrayOutputStream();
        callCommand = new LinkedList<>();
        argsList = new LinkedList<>();
        appRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
    }

    @AfterEach
    void tearDown() throws IOException {
        inputStream.close();
        outputStream.close();
    }

    @Test
    public void testPipeBetweenApplicationsNoException() throws AbstractApplicationException, ShellException {
        String expectedResult = "test";
        callCommand.add(new CallCommandStub(argsList, appRunner, argumentResolver, CallCommandStub.CallCommandT.FIRST_COMMAND));
        callCommand.add(new CallCommandStub(argsList, appRunner, argumentResolver, CallCommandStub.CallCommandT.SECOND_COMMAND));
        PipeCommand pipeCommand = new PipeCommand(callCommand);
        pipeCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    public void testPipeBetweenApplicationsFirstCommandThrowException() throws AbstractApplicationException, ShellException {
        String expectedResult = "shell: First Input ShellException";
        callCommand.add(new CallCommandStub(argsList, appRunner, argumentResolver, CallCommandStub.CallCommandT.FIRST_INPUT_THROW_EXCEPTION));
        callCommand.add(new CallCommandStub(argsList, appRunner, argumentResolver, CallCommandStub.CallCommandT.SECOND_COMMAND));
        PipeCommand pipeCommand = new PipeCommand(callCommand);
        Throwable thrown = assertThrows(ShellException.class, ()-> pipeCommand.evaluate(inputStream, outputStream));
        assertEquals(thrown.getMessage(),expectedResult);
    }

    @Test
    public void testPipeBetweenApplicationsSecondCommandThrowException() throws AbstractApplicationException, ShellException {
        String expectedResult = "shell: Second Input ShellException";
        callCommand.add(new CallCommandStub(argsList, appRunner, argumentResolver, CallCommandStub.CallCommandT.FIRST_COMMAND));
        callCommand.add(new CallCommandStub(argsList, appRunner, argumentResolver, CallCommandStub.CallCommandT.SECOND_INPUT_THROW_EXCEPTION));
        PipeCommand pipeCommand = new PipeCommand(callCommand);
        Throwable thrown = assertThrows(ShellException.class, ()-> pipeCommand.evaluate(inputStream, outputStream));
        assertEquals(thrown.getMessage(),expectedResult);
    }

    @Test
    public void testPipeBetweenApplicationsBothCommandThrowException() throws AbstractApplicationException, ShellException {
        String expectedResult = "shell: First Input ShellException";
        callCommand.add(new CallCommandStub(argsList, appRunner, argumentResolver, CallCommandStub.CallCommandT.FIRST_INPUT_THROW_EXCEPTION));
        callCommand.add(new CallCommandStub(argsList, appRunner, argumentResolver, CallCommandStub.CallCommandT.SECOND_INPUT_THROW_EXCEPTION));
        PipeCommand pipeCommand = new PipeCommand(callCommand);
        Throwable thrown = assertThrows(ShellException.class, ()-> pipeCommand.evaluate(inputStream, outputStream));
        assertEquals(thrown.getMessage(),expectedResult);
    }

}