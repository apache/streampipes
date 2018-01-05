package org.streampipes.pe.axoom.hmi.config;


import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum SourceConfig implements PeConfig {
    INSTANCE;

    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String KAFKA_HOST = "kafka_host";
	private final static String KAFKA_PORT = "kafka_port";
    private static String iconUrl;

    private final static String SERVICE_ID = "pe/org.streampipes.pe.axoom.hmi";
    private final static String SERVICE_NAME = "service_name";

    SourceConfig() {
        config = SpConfig.getSpConfig(SERVICE_ID);
        config.register(HOST, "axoom-hmi", "Hostname for the pe axoom hmi");
        config.register(PORT, 8090, "Port for the pe axoom hmi");
       	config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe axoom hmi project");
        config.register(KAFKA_PORT, 9092, "Port for kafka of the pe axoom hmi project");

        config.register(SERVICE_NAME, "Sources axoom hmi", "The name of the service");


    }

    static {
		iconUrl = SourceConfig.INSTANCE.getHost() + ":" + SourceConfig.INSTANCE.getPort() +"/img/pe_icons";
	}

    public static final String getIconUrl(String pictureName) {
        return iconUrl +pictureName +".png";
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

    @Override
    public String getId() {
        return SERVICE_ID;
    }

    @Override
    public String getName() {
        return config.getString(SERVICE_NAME);
    }

}
