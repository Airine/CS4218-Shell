package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandBuilderTest {

    @Test
    void testParseEmptyCommand() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("   ", new ApplicationRunner());
        });
    }

    @Test
    void testParseInvalidCommand() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("|||;", new ApplicationRunner());
        });
    }


    @Test
    void testParseEmptyPipe() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("|echo", new ApplicationRunner());
        });
    }

    @Test
    void testParseEmptySemicolon() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand(";echo", new ApplicationRunner());
        });
    }

    @Test
    void testParseNewLineCommand() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("\n", new ApplicationRunner());
        });
    }

    @Test
    void testParseNotPairQuoteCommand() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("  rm \"a ", new ApplicationRunner());
        });
    }

    @Test
    void testParseCallCommand() {
        final Command[] command = new Command[1];
        assertDoesNotThrow(() -> {
            command[0] = CommandBuilder.parseCommand("echo hello", new ApplicationRunner());
        });
        assertEquals(command[0].getClass(), CallCommand.class);
        CallCommand callCommand = (CallCommand) command[0];
        Object[] objects = {"echo", "hello"};
        assertArrayEquals(objects, callCommand.getArgsList().toArray());
    }

    @Test
    void testParseCallCommandRedirectOut() {
        final Command[] command = new Command[1];
        assertDoesNotThrow(() -> {
            command[0] = CommandBuilder.parseCommand("echo > hello", new ApplicationRunner());
        });
        assertEquals(command[0].getClass(), CallCommand.class);
    }

    @Test
    void testParseCallCommandRedirectIn() {
        final Command[] command = new Command[1];
        assertDoesNotThrow(() -> {
            command[0] = CommandBuilder.parseCommand("echo < hello", new ApplicationRunner());
        });
        assertEquals(command[0].getClass(), CallCommand.class);
    }

    @Test
    void testParsePipeCommand() {
        final Command[] command = new Command[1];
        assertDoesNotThrow(() -> {
            command[0] = CommandBuilder.parseCommand("echo|grep", new ApplicationRunner());
        });
        assertEquals(command[0].getClass(), PipeCommand.class);

        List<CallCommand> commands = ((PipeCommand) command[0]).getCallCommands();
        assertEquals(2, commands.size());
        assertArrayEquals(new Object[]{"echo"}, commands.get(0).getArgsList().toArray());
        assertArrayEquals(new Object[]{"grep"}, commands.get(1).getArgsList().toArray());
    }

    @Test
    void testParsePipeSequenceCommand() {
        final Command[] command = new Command[1];
        assertDoesNotThrow(() -> {
            command[0] = CommandBuilder.parseCommand("echo|cat;grep", new ApplicationRunner());
        });
        assertEquals(command[0].getClass(), SequenceCommand.class);
        List<Command> commands = ((SequenceCommand) command[0]).getCommands();
        assertEquals(2, commands.size());
        PipeCommand pipeCommand = (PipeCommand) commands.get(0);
        assertArrayEquals(new Object[]{"echo", "cat"}, pipeCommand.getCallCommands().stream().map(
                comm -> comm.getArgsList().get(0)
        ).toArray());

        CallCommand callCommand2 = (CallCommand) commands.get(1);
        assertArrayEquals(new Object[]{"grep"}, callCommand2.getArgsList().toArray());
    }


    @Test
    void testParseSequenceCommand() {
        final Command[] command = new Command[1];
        assertDoesNotThrow(() -> {
            command[0] = CommandBuilder.parseCommand("rm;cat", new ApplicationRunner());
        });
        assertEquals(command[0].getClass(), SequenceCommand.class);
        List<Command> commands = ((SequenceCommand) command[0]).getCommands();
        assertEquals(2, commands.size());
        CallCommand callCommand1 = (CallCommand) commands.get(0);
        assertArrayEquals(new Object[]{"rm"}, callCommand1.getArgsList().toArray());

        CallCommand callCommand2 = (CallCommand) commands.get(1);
        assertArrayEquals(new Object[]{"cat"}, callCommand2.getArgsList().toArray());
    }
}