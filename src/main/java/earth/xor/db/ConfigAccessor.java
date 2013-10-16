package earth.xor.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigAccessor {

    private static Properties properties;
    private static ConfigAccessor instance = null;

    private ConfigAccessor() {}

    public static ConfigAccessor getInstance() {
        if (instance == null) {
            instance = new ConfigAccessor();
            properties = new Properties();
            try {
                properties.load(new FileInputStream("config.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public String getMongoUri() {
        return properties.getProperty("uri");
    }

    public String getDatabaseName() {
        return properties.getProperty("dbname");
    }
}
