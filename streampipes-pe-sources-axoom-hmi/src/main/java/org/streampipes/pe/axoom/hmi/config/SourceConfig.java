package org.streampipes.pe.axoom.hmi.config;


import org.streampipes.config.SpConfig;

/**
 * Created by riemer on 21.04.2017.
 */
public enum SourceConfig {
    INSTANCE;

    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String KAFKA_HOST = "kafka_host";
	private final static String KAFKA_PORT = "kafka_port";
    private static String iconUrl;

    SourceConfig() {
        config = SpConfig.getSpConfig("pe/org.streampipes.pe.axoom.hmi");
        config.register(HOST, "axoom-hmi", "Hostname for the pe axoom hmi");
        config.register(PORT, 8090, "Port for the pe axoom hmi");
       	config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe axoom hmi project");
        config.register(KAFKA_PORT, 9092, "Port for kafka of the pe axoom hmi project");
    }

    static {
		iconUrl = SourceConfig.INSTANCE.getHost() + ":" + SourceConfig.INSTANCE.getPort() +"/img";
	}

    public static final String getIconUrl(String pictureName) {
        return iconUrl +pictureName +".png";
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


}
