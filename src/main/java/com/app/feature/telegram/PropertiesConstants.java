package com.app.feature.telegram;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesConstants {
    private static final String propertiesPath = "./src/main/resources/application.properties";
    public String propertiesReader(String propertyName) {
        Properties property = new Properties();
        String propertyValue = null;
        try {
            FileInputStream inputStream = new FileInputStream(propertiesPath);
            property.load(inputStream);
            propertyValue = property.getProperty(propertyName);
        } catch (IOException e) {
            System.err.println("ERROR: properties file does not exist!");
        }
        return propertyValue;
    }
}
