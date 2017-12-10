package org.streampipes.pe.slack.config;


import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum SlackConfig implements PeConfig {
    INSTANCE;

    public static final String SLACK_NOT_INITIALIZED = "slack_token_not_initialized";

    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String KAFKA_HOST = "kafka_host";
	private final static String KAFKA_PORT = "kafka_port";
	private final static String ZOOKEEPER_HOST = "zookeeper_host";
	private final static String ZOOKEEPER_PORT = "zookeeper_port";

	private final static String SLACK_TOKEN = "slack_token";

    public final static String serverUrl;
    public final static String iconBaseUrl;

    private final static String SERVICE_ID = "pe/org.streampipes.pe.slack";
    private final static String SERVICE_NAME = "service_name";

    SlackConfig() {
        config = SpConfig.getSpConfig(SERVICE_ID);
       	config.register(HOST, "slack", "Hostname for the pe slack integration");
        config.register(PORT, 8090, "Port for the pe slack integration");
       	config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe sinks project");
        config.register(KAFKA_PORT, 9092, "Port for kafka of the pe sinks project");
       	config.register(ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe sinks project");
        config.register(ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe sinks project");
        config.register(SLACK_TOKEN, SLACK_NOT_INITIALIZED, "Token for the slack bot. Must be generated in slack");

        config.register(SERVICE_NAME, "Slack", "The name of the service");

    }

    static {
		serverUrl = SlackConfig.INSTANCE.getHost() + ":" + SlackConfig.INSTANCE.getPort();
		iconBaseUrl = SlackConfig.INSTANCE.getHost() + ":" + SlackConfig.INSTANCE.getPort() +"/img/pe_icons";
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

    public String getSlackToken() {
        return config.getString(SLACK_TOKEN);
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
