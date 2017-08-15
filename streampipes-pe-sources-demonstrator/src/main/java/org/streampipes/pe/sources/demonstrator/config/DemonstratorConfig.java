package org.streampipes.pe.sources.demonstrator.config;

import org.streampipes.config.SpConfig;

public enum DemonstratorConfig {
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
	public final static String eventReplayURI;
	public final static String topicPrefixDdm;
	public final static String topicPrefixRam;

    DemonstratorConfig() {
		config = SpConfig.getSpConfig("pe/org.streampipes.pe.sources.demonstrator");
		config.register(HOST, "slack", "Hostname for the pe slack integration");
		config.register(PORT, 8090, "Port for the pe slack integration");
		       	config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe demonstrator project");
        config.register(KAFKA_PORT, 9092, "Port for kafka of the pe demonstrator project");
       	config.register(ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe demonstrator project");
        config.register(ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe demonstrator project");
	}

	static {
   		serverUrl = DemonstratorConfig.INSTANCE.getHost() + ":" + DemonstratorConfig.INSTANCE.getPort();
		iconBaseUrl = DemonstratorConfig.INSTANCE.getHost() + ":" + DemonstratorConfig.INSTANCE.getPort() +"/img";
		eventReplayURI = "http://89.216.116.44:8084";
		topicPrefixDdm = "SEPA.SEP.DDM.";
		topicPrefixRam = "SEPA.SEP.Ram";
	}

    public String getHost() {
        return config.getString(HOST);
    }

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


}
