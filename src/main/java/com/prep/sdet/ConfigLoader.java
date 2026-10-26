package com.prep.sdet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Snippet 14: IO / config. Format: key=value per line.
 * Blank lines and lines starting with '#' are ignored. Keys and values are trimmed.
 * Only the first '=' splits, so values may contain '='. Later duplicates override earlier ones.
 * A non-blank, non-comment line without '=' throws ConfigException naming the line number.
 * Missing file throws ConfigException.
 * getInt: returns default if key absent; ConfigException if value is not an integer.
 * require: ConfigException if key absent.
 */
public class ConfigLoader {
    private final Map<String, String> values = new HashMap<>();

    private ConfigLoader() {}

    public static ConfigLoader load(Path path) {
        if (path == null) throw new IllegalArgumentException("null path");
        if (!Files.isRegularFile(path)) throw new ConfigException("config file not found: " + path);
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new ConfigException("cannot read " + path, e);
        }
        ConfigLoader cfg = new ConfigLoader();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            int eq = line.indexOf('=');
            if (eq < 0) throw new ConfigException("line " + (i + 1) + ": expected key=value");
            cfg.values.put(line.substring(0, eq).trim(), line.substring(eq + 1).trim());
        }
        return cfg;
    }

    public String get(String key, String defaultValue) { return values.getOrDefault(key, defaultValue); }

    public String require(String key) {
        String v = values.get(key);
        if (v == null) throw new ConfigException("missing required key: " + key);
        return v;
    }

    public int getInt(String key, int defaultValue) {
        String v = values.get(key);
        if (v == null) return defaultValue;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new ConfigException("key " + key + " is not an integer: " + v, e);
        }
    }
}
