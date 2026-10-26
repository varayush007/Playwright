package com.prep.sdet.solutions;

import static com.prep.sdet.CsvLineParser.parse;
import static org.testng.Assert.*;

import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CsvLineParserTest {

    @DataProvider(name = "csvCases")
    public Object[][] csvCases() {
        return new Object[][]{
                {"a,b,c", List.of("a", "b", "c")},
                {"single", List.of("single")},
                {"", List.of("")},                                    // empty line, one empty field
                {"a,", List.of("a", "")},                             // trailing comma
                {",a", List.of("", "a")},                             // leading comma
                {",,", List.of("", "", "")},                          // consecutive delimiters
                {" a , b ", List.of(" a ", " b ")},                   // spaces preserved
                {"\"a,b\",c", List.of("a,b", "c")},                   // comma inside quotes
                {"\"say \"\"hi\"\"\",x", List.of("say \"hi\"", "x")}, // escaped quote
                {"\"\"", List.of("")},                                // quoted empty
                {"\"\",\"\"", List.of("", "")},
                {"a,\"b\",c", List.of("a", "b", "c")},                // quotes around plain field
        };
    }

    @Test(dataProvider = "csvCases")
    public void parse_returnsExpectedFields(String line, List<String> expected) {
        assertEquals(parse(line), expected);
    }

    @Test
    public void parse_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> parse(null));
    }

    @DataProvider(name = "unterminated")
    public Object[][] unterminated() {
        return new Object[][]{{"\"abc"}, {"a,\"b,c"}, {"\"a\"\""}};
    }

    @Test(dataProvider = "unterminated")
    public void parse_unterminatedQuote_throws(String line) {
        assertThrows(IllegalArgumentException.class, () -> parse(line));
    }
}
