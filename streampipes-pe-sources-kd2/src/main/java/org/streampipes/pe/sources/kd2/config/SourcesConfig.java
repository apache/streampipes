package org.streampipes.pe.sources.kd2.config;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

/**
 * Created by riemer on 18.11.2016.
 */
public enum SourcesConfig implements PeConfig {
    INSTANCE;

    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String KAFKA_HOST = "kafka_host";
	private final static String KAFKA_PORT = "kafka_port";
	private final static String ZOOKEEPER_HOST = "zookeeper_host";
	private final static String ZOOKEEPER_PORT = "zookeeper_port";

    public final static String serverUrl;
    public final static String iconBaseUrl;
    public final static String topicPrefixBiodata;

    private final static String SERVICE_ID = "pe/org.streampipes.pe.sources.kd2";
    private final static String SERVICE_NAME = "service_name";

   	SourcesConfig() {
		config = SpConfig.getSpConfig(SERVICE_ID);
		config.register(HOST, "sources-hella", "Hostname for the pe sources kd2");
		config.register(PORT, 8090, "Port for the pe sources kd2");
       	config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe kd2 project");
        config.register(KAFKA_PORT, 9092, "Port for kafka of the pe kd2 project");
       	config.register(ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe kd2 project");
        config.register(ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe kd2 project");

        config.register(SERVICE_NAME, "Sources kd2", "The name of the service");


    }


	static {
    	serverUrl = SourcesConfig.INSTANCE.getHost() + ":" + SourcesConfig.INSTANCE.getPort();
		iconBaseUrl = SourcesConfig.INSTANCE.getHost() + ":" + SourcesConfig.INSTANCE.getPort() +"/img/pe_icons";

        topicPrefixBiodata = "kd2.biodata.";
    }

    @Override
    public String getHost() {
        return config.getString(HOST);
    }

    @Override
    public int getPort() {
        return config.getInteger(PORT);
    }

    public String getKafkaHost() {
        return config.getString(KAFKA_HOST);
    }

    public int getKafkaPort() {
        return config.getInteger(KAFKA_PORT);
    }

    public String getKafkaUrl() {
        return getKafkaHost() + ":" + getKafkaPort();
    }

    public String getZookeeperHost() {
        return config.getString(ZOOKEEPER_HOST);
    }

    public int getZookeeperPort() {
        return config.getInteger(ZOOKEEPER_PORT);
    }

    @Override
    public String getId() {
        return SERVICE_ID;
    }

    @Override
    public String getName() {
        return config.getString(SERVICE_NAME);
    }

}
