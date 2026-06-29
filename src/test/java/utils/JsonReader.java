package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonReader {

    private static JsonNode rootNode;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            rootNode = mapper.readTree(new File("src/test/resources/testdata.json"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read testdata.json", e);
        }
    }

    public static String getValue(String objectName, String key) {
        return rootNode.get(objectName).get(key).asText();
    }
}