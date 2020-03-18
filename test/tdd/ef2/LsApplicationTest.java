    package tdd.ef2;

    import org.junit.jupiter.api.*;
    import sg.edu.nus.comp.cs4218.Environment;
    import sg.edu.nus.comp.cs4218.exception.LsException;
    import sg.edu.nus.comp.cs4218.impl.app.LsApplication;
    import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
    import tdd.util.TestUtil;

    import java.io.ByteArrayOutputStream;
    import java.io.IOException;
    import java.io.OutputStream;
    import java.nio.file.Files;

    import static org.junit.jupiter.api.Assertions.*;
    import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
    import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

    /**
     * Test Suite for ls command
     * <p>
     * Contains negative and positive test cases
     */
    class LsApplicationTest {

        public static final String TEXTFILE_TXT = "textfile.txt";
        public static final String FOLDER1 = "folder1";
        public static final String FOLDER2 = "folder2";
        public static final String FILE1_FOLDER2 = "file1Folder2.txt";
        private static LsApplication lsApp;
        private static OutputStream stdout;

        private static final String ORIGINAL_DIR = Environment.currentDirectory;


        @BeforeAll
        static void setupAll() {
            String path = ORIGINAL_DIR
                    + StringUtils.fileSeparator()+ "src"
                    + StringUtils.fileSeparator()+ "test"
                    + StringUtils.fileSeparator() + "tdd"
                    + StringUtils.fileSeparator() + "util"
                    + StringUtils.fileSeparator() + "dummyTestFolder"
                    + StringUtils.fileSeparator() + "LsTestFolder";
            if (Files.isDirectory(TestUtil.resolveFilePath(path))) {
                Environment.currentDirectory = TestUtil.resolveFilePath(path).toString();
            }
        }

        @AfterAll
        static void reset() {
            Environment.currentDirectory = ORIGINAL_DIR;
        }


        @BeforeEach
        void setUp() {
            lsApp = new LsApplication();
            stdout = new ByteArrayOutputStream();
        }

        @AfterEach
        void tearDown() throws IOException {
            stdout.flush();
        }

        /**
         *  Tests if fails properly when null stdout or no args passed
         */
        @Test
        public void testFailsWithNullArgsOrStream() {
            Exception expectedException = assertThrows(LsException.class, () -> lsApp.run(null, null, stdout));
            assertTrue(expectedException.getMessage().contains(ERR_NULL_ARGS));

            expectedException = assertThrows(LsException.class, () -> lsApp.run(new String[0], null, null));
            assertTrue(expectedException.getMessage().contains(ERR_NO_OSTREAM));

        }

        /**
         * Throws error when no existent directory with that name
         * @throws LsException
         */
        @Test
        public void testNonExistentDirectory() throws LsException {
            lsApp.run(new String[]{"no-folder-named-like-this" }, System.in, stdout);

            assertTrue(stdout.toString().contains("ls: cannot access 'no-folder-named-like-this': No such file or directory"));

        }

        /**
         * Test with illegal flag -o
         */
        @Test
        public void testIllegalFlagGiven() {
            Exception expectedException = assertThrows(LsException.class, () -> lsApp.run(new String[]{"-o"}, null, stdout));
            assertTrue(expectedException.getMessage().contains("illegal option"));

        }


        /**
         * LS command with no argument showing non-null result
         * @throws LsException
         */
        @Test
        void testLsWithNoArgs() {
            try {
                lsApp.run(new String[0], System.in, stdout);
                assertTrue(stdout.toString().contains(TEXTFILE_TXT));
            } catch (LsException e) {
                fail("should not fail:" + e.getMessage());
            }
        }

        /**
         * LS command with -d argument showing a directory
         * @throws LsException
         */
        @Test
        void testLsOnlyFolders() throws LsException {

            lsApp.run(new String[]{"-d"}, System.in, stdout);

            assertTrue(stdout.toString().contains(FOLDER1 + StringUtils.STRING_NEWLINE + FOLDER2));
        }

        /**
         * LS command with -R argument showing all recursive directory and files
         * @throws LsException
         */
        @Test
        void testLsRecursiveDirectory() throws LsException {

            lsApp.run(new String[]{"-R"}, System.in, stdout);

            assertTrue(stdout.toString().contains("folder2:"  + StringUtils.STRING_NEWLINE + FILE1_FOLDER2));
            assertTrue(stdout.toString().contains(FOLDER1));
        }

        /**
         * LS command with -R and -d argument showing all recursive directory and files of only folders
         * @throws LsException
         */
        @Test
        void testLsRecursiveFolderOnlyDirectory() throws LsException {

            lsApp.run(new String[]{"-R", "-d"}, System.in, stdout);

            assertTrue(stdout.toString().contains(FOLDER1 + StringUtils.STRING_NEWLINE + FOLDER2));
            assertFalse(stdout.toString().contains(FILE1_FOLDER2));
        }

        /**
         * LS command with directory specified
         * @throws LsException
         */
        @Test
        void testLsAtAnotherDirectory() throws LsException {

            lsApp.run(new String[]{FOLDER2}, System.in, stdout);

            assertTrue(stdout.toString().contains(FILE1_FOLDER2));
        }

        /**
         * LS command with file specified
         * @throws LsException
         */
        @Test
        void testLsWithFileSpecified() throws LsException {

            lsApp.run(new String[]{TEXTFILE_TXT}, System.in, stdout);

            assertTrue(stdout.toString().contains(TEXTFILE_TXT));
        }

        /**
         * LS command with file name having spaces
         * @throws LsException
         */
        @Test
        void testLsWithFileNameSpaces() throws LsException {

            lsApp.run(new String[]{"file with spaces.txt"}, System.in, stdout);

            assertTrue(stdout.toString().contains("file with spaces.txt"));
        }

        /**
         * Tests helper function listFolderContent() recursively
         * @throws LsException
         */
        @Test
        public void testListFolderContent() throws LsException {
            String result = lsApp.listFolderContent(false, true, "folderRecursive");
            assertTrue(result.contains("folderRecursive:" + StringUtils.STRING_NEWLINE
                    + "firstFile.txt" + StringUtils.STRING_NEWLINE + "innerFolder"));

            assertTrue(result.contains("folderRecursive/innerFolder:" + StringUtils.STRING_NEWLINE + "innerFile.txt"));
        }


    }