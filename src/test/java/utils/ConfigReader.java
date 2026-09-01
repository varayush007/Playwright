package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();
    static {
        try(FileInputStream file = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }

    public static String get(String key){
        //from system basically from maven command
        String systemValue = System.getProperty(key);
        if(systemValue != null)
            return systemValue;
        //config properties se
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(
                    "Configuration key not found: " + key
            );
        }
        return value;
    }
}
