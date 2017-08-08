package org.streampipes.config.backend;


import org.streampipes.config.SpConfig;

public enum BackendConfig {
    INSTANCE;

    private SpConfig config;
   	private final static String JMS_HOST = "jms_host";
	private final static String JMS_PORT = "jms_port";
    private final static String KAFKA_HOST = "kafka_host";
	private final static String KAFKA_PORT = "kafka_port";
	private final static String ZOOKEEPER_HOST = "zookeeper_host";
	private final static String ZOOKEEPER_PORT = "zookeeper_port";
	private final static String IS_CONFIGURED = "is_configured";

	BackendConfig() {
   		config = SpConfig.getSpConfig("backend");

   		config.register(JMS_HOST, "activemq", "Hostname for backend service for active mq");
        config.register(JMS_PORT, 9092, "Port for backend service for active mq");
      	config.register(KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
        config.register(KAFKA_PORT, 9092, "Port for backend service for kafka");
       	config.register(ZOOKEEPER_HOST, "zookeeper", "Hostname for backend service for zookeeper");
        config.register(ZOOKEEPER_PORT, 2181, "Port for backend service for zookeeper");
		config.register(IS_CONFIGURED, false, "Boolean that indicates whether streampipes is " +
				"already configured or not");
    }

    public String getJmsHost() {
		return config.getString(JMS_HOST);
	}

	public int getJmsPort() {
		return config.getInteger(JMS_PORT);
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

	public boolean isConfigured() {
		return config.getBoolean(IS_CONFIGURED);
	}

	public void setKafkaHost(String s) {
		config.setString(KAFKA_HOST, s);
	}

	public void setZookeeperHost(String s) {
		config.setString(ZOOKEEPER_HOST, s);
	}

	public void setJmsHost(String s) {
		config.setString(JMS_HOST, s);
	}

}
