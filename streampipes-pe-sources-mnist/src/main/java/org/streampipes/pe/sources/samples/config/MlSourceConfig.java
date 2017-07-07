package org.streampipes.pe.sources.samples.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;

public enum MlSourceConfig {
    INSTANCE;

    private Boolean withLabel;

    private File configFile;
    private PropertiesConfiguration propertiesConfiguration;

    MlSourceConfig() {
        configFile = AdapterConfigurationManager.getAdapterConfigFile();
        loadConfiguration();
    }

    private void loadConfiguration() {
        try {
            propertiesConfiguration = new PropertiesConfiguration(configFile);

            this.withLabel = propertiesConfiguration.getBoolean("withLabel");

        } catch (ConfigurationException e) {
            e.printStackTrace();
            this.withLabel = true;
       }
    }

    public Boolean getWithLabel() {
        return withLabel;
    }

    public void setWithLabel(Boolean withLabel) {
        this.withLabel = withLabel;
    }
}
