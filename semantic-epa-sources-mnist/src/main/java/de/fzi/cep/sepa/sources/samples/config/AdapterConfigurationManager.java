package de.fzi.cep.sepa.sources.samples.config;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Created by riemer on 18.11.2016.
 */
public class AdapterConfigurationManager {

    private static String getAdapterConfigFilePath() {
        return getAdapterConfigFileLocation() + getStreamPipesClientConfigFilename();
    }

    public static String getAdapterConfigFileLocation() {
        return System.getProperty("user.home") + File.separator + ".streampipes" + File.separator;
    }


    private static String getStreamPipesClientConfigFilename() {
        return "streampipes-sources-ml.config";
    }

    public static File getAdapterConfigFile() {
        File file = new File(getAdapterConfigFilePath());
        if (!file.exists()) {
            createDefaultConfig();
            return new File(getAdapterConfigFilePath());
        }
        else {
            return file;
        }
    }

    private static void createDefaultConfig() {

        Properties properties = new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };

        File pathToFile = new File(getAdapterConfigFileLocation());
        File configFile = new File(getAdapterConfigFilePath());
        if (!pathToFile.exists()) pathToFile.mkdir();
        if (!configFile.exists())
            try {
                configFile.createNewFile();
                properties.store(new FileWriter(configFile), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
