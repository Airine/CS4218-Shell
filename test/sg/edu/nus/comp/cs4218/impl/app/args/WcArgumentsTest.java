package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WcArgumentsTest {

    private WcArguments wcArguments = new WcArguments();

    @Test
    void parseWithEmptyArg() {
        assertDoesNotThrow(() -> {
            wcArguments.parse("");
        });
    }

    @Test
    void parseWithBytesOption() {
        assertDoesNotThrow(() -> {
            wcArguments.parse("-c");
        });
    }

    @Test
    void parseWithLinesOption() {
        assertDoesNotThrow(() -> {
            wcArguments.parse("-l");
        });
    }

    @Test
    void parseWithWordsOption() {
        assertDoesNotThrow(() -> {
            wcArguments.parse("-w");
        });
    }

    @Test
    void parseWithThreeOptions() {
        assertDoesNotThrow(() -> {
            wcArguments.parse("-clw");
        });
    }


    @Test
    void parseWithThreeSeperatedOptions() {
        assertDoesNotThrow(() -> {
            wcArguments.parse("-c", "-l", "-w");
        });
    }

    @Test
    void parseWithInvalidArg() {
        assertThrows(Exception.class, () -> {
           wcArguments.parse("-a");
        });
    }
}