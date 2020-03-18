package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SedArgumentsTest {

    private final SedArguments sedArguments = new SedArguments();

    @Test
    void validateWithNullArgs() {
        assertThrows(Exception.class, () -> {
            SedArguments.validate(null, null, 0);
        });
    }

    @Test
    void parseWithInvalidNumber() {
        assertThrows(Exception.class, () -> {
            sedArguments.parse("s/a/b/2-2");
        });
    }
}