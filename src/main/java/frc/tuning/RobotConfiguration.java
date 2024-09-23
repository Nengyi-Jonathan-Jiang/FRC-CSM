package frc.tuning;

import edu.wpi.first.wpilibj.Filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class RobotConfiguration {
    private static final String basePath = Filesystem.getDeployDirectory().getPath();
    private static final Map<String, ValueSource> values = new HashMap<>();

    private RobotConfiguration() {}

    public static void loadFile(String fileName) {
        try {
            String fileContents = new String(Files.readAllBytes(Paths.get(basePath, fileName)));
            new RobotConfigurationParser(values).parse(fileContents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateAll(){
        
    }

    public static NumberSource getNumber(String key) {
        ValueSource entry = values.get(key);
        if (entry == null) {
            throw new Error("Could not find config value for key " + key);
        }
        if (entry instanceof NumberSource numberEntry) {
            return numberEntry;
        }
        throw new Error("Value for key " + key + " is a not a number");
    }

    public static TableSource getTable(String key) {
        ValueSource entry = values.get(key);
        if (entry == null) {
            throw new Error("Could not find config value for key " + key);
        }
        if (entry instanceof TableSource tableEntry) {
            return tableEntry;
        }
        throw new Error("Value for key " + key + " is a not a table");
    }

}