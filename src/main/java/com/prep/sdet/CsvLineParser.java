package com.prep.sdet;

import java.util.ArrayList;
import java.util.List;

/**
 * Snippet 8a: CSV line parser.
 * Splits on commas. A field may be wrapped in double quotes to contain commas;
 * a doubled quote ("") inside a quoted field is a literal quote.
 * Empty line returns one empty field. null throws IAE. Unterminated quote throws IAE.
 */
public final class CsvLineParser {
    private CsvLineParser() {}

    public static List<String> parse(String line) {
        if (line == null) throw new IllegalArgumentException("null");
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        if (inQuotes) throw new IllegalArgumentException("unterminated quote");
        fields.add(cur.toString());
        return fields;
    }
}
