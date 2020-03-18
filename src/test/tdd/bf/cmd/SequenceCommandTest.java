package tdd.bf.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;
import tdd.util.CommandStub;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

/**
 * SequenceCommand takes in a list of CallCommands / PipeCommands. Run the first command; when the first command
 * terminates, run the second command. If an exception is thrown during the execution of the first command, the
 * execution of the second command can continue and may return a non-zero exit code.
 **/

class SequenceCommandTest {

    List<Command> commands;
    InputStream inputStream;
    OutputStream outputStream;
    SequenceCommand sequenceCommand;

    @BeforeEach
    void setUp() {
        commands = new LinkedList<>();
        inputStream = new ByteArrayInputStream("".getBytes());
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() throws IOException {
        inputStream.close();
        outputStream.close();
    }

    @Test
    public void testTwoCommandNoExceptionThrown() throws AbstractApplicationException, ShellException {

        String expectedResult = "test " + "test ";
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    public void testFirstCommandExceptionThrown() throws AbstractApplicationException, ShellException {

        String expectedResult = "echo: EchoException" + STRING_NEWLINE + "test ";
        commands.add(new CommandStub(CommandStub.CommandT.ECHO_EXCEPTION));
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    public void testSecondCommandExceptionThrown() throws AbstractApplicationException, ShellException {

        String expectedResult = "test " + "echo: EchoException" + STRING_NEWLINE;
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        commands.add(new CommandStub(CommandStub.CommandT.ECHO_EXCEPTION));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    public void testTwoCommandTwoExceptionThrown() throws AbstractApplicationException, ShellException {

        String expectedResult = "echo: EchoException" + STRING_NEWLINE + "echo: EchoException" + STRING_NEWLINE;
        commands.add(new CommandStub(CommandStub.CommandT.ECHO_EXCEPTION));
        commands.add(new CommandStub(CommandStub.CommandT.ECHO_EXCEPTION));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());

    }

    @Test
    public void testFirstCommandEmpty() throws AbstractApplicationException, ShellException {
        String expectedResult = STRING_NEWLINE + "test ";
        commands.add(new CommandStub(CommandStub.CommandT.WHITESPACE));
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    public void testSecondCommandEmpty() throws AbstractApplicationException, ShellException {
        String expectedResult = "test " + STRING_NEWLINE;
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        commands.add(new CommandStub(CommandStub.CommandT.WHITESPACE));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    public void testBothCommandsEmpty() throws AbstractApplicationException, ShellException {
        String expectedResult = STRING_NEWLINE + STRING_NEWLINE;
        commands.add(new CommandStub(CommandStub.CommandT.WHITESPACE));
        commands.add(new CommandStub(CommandStub.CommandT.WHITESPACE));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    public void testThreeCommands() throws AbstractApplicationException, ShellException {

        String expectedResult = "test " + "test " + "test ";
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());

    }

    @Test
    public void testThreeCommandsMiddleThrowException() throws AbstractApplicationException, ShellException {

        String expectedResult = "test " + "echo: EchoException" + STRING_NEWLINE + "test ";
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        commands.add(new CommandStub(CommandStub.CommandT.ECHO_EXCEPTION));
        commands.add(new CommandStub(CommandStub.CommandT.PRINT_TO_STDOUT));
        sequenceCommand = new SequenceCommand(commands);
        sequenceCommand.evaluate(inputStream, outputStream);
        assertEquals(expectedResult, outputStream.toString());

    }

}