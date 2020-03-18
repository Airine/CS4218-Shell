package tdd.bf.cmd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.AbstractNaming"})
public class BaseCommandTest {

    /**
     * Pass the data from an input stream to an output stream
     *
     **/
    void transferFromInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    String getStringFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8.name());
    }
}
