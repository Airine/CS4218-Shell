package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IORedirectionHandlerTest {

    @Test
    void testGetNoRedirArgsList(){
        List<String> args = Arrays.asList("a", "b");
        ArgumentResolver resolver = new ArgumentResolver();
        IORedirectionHandler handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertDoesNotThrow(()->{
            handler.extractRedirOptions();
            assertEquals(Arrays.asList("a", "b"), handler.getNoRedirArgsList());
        });
    }

    @Test
    void testGetInputStream(){
        List<String> args = Arrays.asList("a", "b");
        ArgumentResolver resolver = new ArgumentResolver();
        IORedirectionHandler handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertEquals(System.in, handler.getInputStream());
    }

    @Test
    void testGetOutputStream(){
        List<String> args = Arrays.asList("a", "b");
        ArgumentResolver resolver = new ArgumentResolver();
        IORedirectionHandler handler = new IORedirectionHandler(args, System.in, System.out, resolver);
        assertEquals(System.out, handler.getOutputStream());
    }


}