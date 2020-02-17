package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.app.PasteInterface;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class PasteApplicationTest {

    private final PasteInterface app = new PasteApplication();

    @Test
    void testMergeTwoFilesWithSameLineNumber() throws Exception {
        String fileNameA = "asset/A.txt";
        String fileNameB = "asset/B.txt";
        String expectResult = "A"+CHAR_TAB+"1"+CHAR_TAB+STRING_NEWLINE+
                "B"+CHAR_TAB+"2"+CHAR_TAB+STRING_NEWLINE+
                "C"+CHAR_TAB+"3"+CHAR_TAB+STRING_NEWLINE+
                "D"+CHAR_TAB+"4"+CHAR_TAB+STRING_NEWLINE;
        String realResult = app.mergeFile(fileNameA, fileNameB);
        assertEquals(expectResult, realResult);
    }
}