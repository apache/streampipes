package de.fzi.cep.sepa.actions.samples.rabbitmq;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;

/**
 * Created by riemer on 18.11.2016.
 */
public enum AdapterConfiguration {

    INSTANCE;

    private String rabbitMqHost;
    private Integer rabbitMqPort;
    private String rabbitMqUser;
    private String rabbitMqPassword;


    private File configFile;
    private PropertiesConfiguration propertiesConfiguration;

    AdapterConfiguration() {
        configFile = AdapterConfigurationManager.getAdapterConfigFile();
        loadConfiguration();
    }

    private void loadConfiguration() {
        try {
            propertiesConfiguration = new PropertiesConfiguration(configFile);

            this.rabbitMqHost = propertiesConfiguration.getString(StreamPipesConstants
                    .rabbitMqHostKey);
            this.rabbitMqUser = propertiesConfiguration.getString(StreamPipesConstants
                    .rabbitmqUserKey);
            this.rabbitMqPassword = propertiesConfiguration.getString(StreamPipesConstants
                    .rabbitMqPasswordKey);
            this.rabbitMqPort = propertiesConfiguration.getInt(StreamPipesConstants
                    .rabbitMqPortKey);



        } catch (ConfigurationException e) {
            e.printStackTrace();
            this.rabbitMqHost = StreamPipesConstants.rabbitMqHostDefault;
            this.rabbitMqUser = StreamPipesConstants.rabbitMqUserDefault;
            this.rabbitMqPassword = StreamPipesConstants.rabbitMqPasswordDefault;
            this.rabbitMqPort = StreamPipesConstants.rabbitMqPortDefault;
        }
    }

    public String getRabbitMqHost() {
        return rabbitMqHost;
    }

    public String getRabbitMqUser() {
        return rabbitMqUser;
    }

    public String getRabbitMqPassword() {
        return rabbitMqPassword;
    }

    public Integer getRabbitMqPort() {
        return rabbitMqPort;
    }
}
