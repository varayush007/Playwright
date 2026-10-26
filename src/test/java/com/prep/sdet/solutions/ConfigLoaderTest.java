package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.ConfigException;
import com.prep.sdet.ConfigLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ConfigLoaderTest {
    private Path dir;

    @BeforeMethod
    public void setUp() throws IOException { dir = Files.createTempDirectory("cfg"); }

    @AfterMethod
    public void tearDown() throws IOException {
        try (var paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

    private ConfigLoader load(String content) throws IOException {
        Path f = dir.resolve("app.cfg");
        Files.writeString(f, content);
        return ConfigLoader.load(f);
    }

    @Test
    public void load_readsKeyValuePairs() throws IOException {
        ConfigLoader cfg = load("host=localhost\nport=8080\n");
        assertEquals(cfg.get("host", null), "localhost");
        assertEquals(cfg.getInt("port", 0), 8080);
    }

    @Test
    public void load_trimsKeysAndValues() throws IOException {
        assertEquals(load("  host  =  localhost  \n").get("host", null), "localhost");
    }

    @Test
    public void load_ignoresBlankLinesAndComments() throws IOException {
        ConfigLoader cfg = load("# header\n\n   \n   # indented comment\na=1\n");
        assertEquals(cfg.get("a", null), "1");
        assertNull(cfg.get("# header", null));
    }

    @Test
    public void load_valueMayContainEquals() throws IOException {
        assertEquals(load("url=http://x.com?a=b&c=d\n").get("url", null), "http://x.com?a=b&c=d");
    }

    @Test
    public void load_duplicateKey_lastWins() throws IOException {
        assertEquals(load("a=1\na=2\n").get("a", null), "2");
    }

    @Test
    public void load_windowsLineEndings() throws IOException {
        ConfigLoader cfg = load("a=1\r\nb=2\r\n");
        assertEquals(cfg.get("a", null), "1");
        assertEquals(cfg.get("b", null), "2");
    }

    @Test
    public void load_emptyFile_givesEmptyConfig() throws IOException {
        assertEquals(load("").get("a", "dflt"), "dflt");
    }

    @Test // characterization: inline comments are NOT stripped. A clarifying question
    public void load_inlineComment_becomesPartOfValue() throws IOException {
        assertEquals(load("a=1 # note\n").get("a", null), "1 # note");
    }

    @Test
    public void load_lineWithoutEquals_throwsWithLineNumber() {
        ConfigException ex = expectThrows(ConfigException.class, () -> load("a=1\n\nbroken\n"));
        assertTrue(ex.getMessage().contains("line 3"), ex.getMessage());
    }

    @Test
    public void load_missingFile_throwsConfigException() {
        assertThrows(ConfigException.class, () -> ConfigLoader.load(dir.resolve("nope.cfg")));
    }

    @Test
    public void load_directoryInsteadOfFile_throwsConfigException() {
        assertThrows(ConfigException.class, () -> ConfigLoader.load(dir));
    }

    @Test
    public void load_nullPath_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> ConfigLoader.load(null));
    }

    @Test
    public void get_absentKey_returnsDefault() throws IOException {
        assertEquals(load("a=1\n").get("zzz", "fallback"), "fallback");
    }

    @Test
    public void require_present_returnsValue_absent_throws() throws IOException {
        ConfigLoader cfg = load("a=1\nempty=\n");
        assertEquals(cfg.require("a"), "1");
        assertEquals(cfg.require("empty"), "");              // present but empty is still present
        ConfigException ex = expectThrows(ConfigException.class, () -> cfg.require("missing"));
        assertTrue(ex.getMessage().contains("missing"));
    }

    @DataProvider(name = "goodInts")
    public Object[][] goodInts() {
        return new Object[][]{{"n=0", 0}, {"n=-5", -5}, {"n=  42  ", 42}, {"n=2147483647", Integer.MAX_VALUE}};
    }

    @Test(dataProvider = "goodInts")
    public void getInt_validValues(String line, int expected) throws IOException {
        assertEquals(load(line + "\n").getInt("n", -999), expected);
    }

    @DataProvider(name = "badInts")
    public Object[][] badInts() {
        return new Object[][]{{"n=abc"}, {"n="}, {"n=1.5"}, {"n=2147483648"}};
    }

    @Test(dataProvider = "badInts")
    public void getInt_invalidValues_throwConfigException(String line) throws IOException {
        ConfigLoader cfg = load(line + "\n");
        assertThrows(ConfigException.class, () -> cfg.getInt("n", 0));
    }

    @Test
    public void getInt_absentKey_returnsDefault() throws IOException {
        assertEquals(load("a=1\n").getInt("n", 7), 7);
    }
}
