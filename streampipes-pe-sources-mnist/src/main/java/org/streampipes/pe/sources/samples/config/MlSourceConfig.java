package org.streampipes.pe.sources.samples.config;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum MlSourceConfig implements PeConfig {
	INSTANCE;

	private SpConfig config;
	private final static String HOST = "host";
	private final static String PORT = "port";
	private final static String KAFKA_HOST = "kafka_host";
	private final static String KAFKA_PORT = "kafka_port";
	private final static String ZOOKEEPER_HOST = "zookeeper_host";
	private final static String ZOOKEEPER_PORT = "zookeeper_port";
	private final static String JMS_HOST = "jms_host";
	private final static String JMS_PORT = "jms_port";
	private final static String DATA_LOCATION = "data_location";
	private final static String WITH_LABEL = "with_label";

	public final static String serverUrl;
	public final static String iconBaseUrl;
	public final static String eventReplayURI;
	public final static String topicPrefixDdm;
	public final static String topicPrefixRam;

	private final static String SERVICE_ID = "pe/org.streampipes.pe.sources.samples";
	private final static String SERVICE_NAME = "service_name";

	MlSourceConfig() {
		config = SpConfig.getSpConfig(SERVICE_ID);
		config.register(HOST, "pe-mnist", "Hostname for the pe sources samples");
		config.register(PORT, 8090, "Port for the pe sources samples");
		config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe sources samples project");
		config.register(KAFKA_PORT, 9092, "Port for kafka of the pe sources samples project");
		config.register(ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe sources samples project");
		config.register(ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe sources samples project");
		config.register(JMS_HOST, "activemq", "Hostname for pe sources samples service for active mq");
		config.register(JMS_PORT, 9092, "Port for pe sources samples service for active mq");
		config.register(DATA_LOCATION,"/home/user/", "Folder with the data for the replay of streams");
		config.register(WITH_LABEL, false, "When true the data is replayed with a label, when false the label is ignored");

		config.register(SERVICE_NAME, "Sources mnist", "The name of the service");

	}


	static {
		serverUrl = MlSourceConfig.INSTANCE.getHost() + ":" + MlSourceConfig.INSTANCE.getPort();
		iconBaseUrl = MlSourceConfig.INSTANCE.getHost() + ":" + MlSourceConfig.INSTANCE.getPort() + "/img/pe_icons";
		eventReplayURI = "http://89.216.116.44:8084";
		topicPrefixDdm = "SEPA.SEP.DDM.";
		topicPrefixRam = "SEPA.SEP.Ram";
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

	public String getJmsHost() {
		return config.getString(JMS_HOST);
	}

	public int getJmsPort() {
		return config.getInteger(JMS_PORT);
	}

	public String getDataLocation() {
		return config.getString(DATA_LOCATION);
	}

	public boolean isWithLabel() {
		return config.getBoolean(WITH_LABEL);
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

//import org.apache.commons.configuration.ConfigurationException;
//import org.apache.commons.configuration.PropertiesConfiguration;
//
//import java.io.File;
//
//public enum MlSourceConfig {
//    INSTANCE;
//
//    private Boolean withLabel;
//
//    private File configFile;
//    private PropertiesConfiguration propertiesConfiguration;
//
//    MlSourceConfig() {
//        configFile = AdapterConfigurationManager.getAdapterConfigFile();
//        loadConfiguration();
//    }
//
//    private void loadConfiguration() {
//        try {
//            propertiesConfiguration = new PropertiesConfiguration(configFile);
//
//            this.withLabel = propertiesConfiguration.getBoolean("withLabel");
//
//        } catch (ConfigurationException e) {
//            e.printStackTrace();
//            this.withLabel = true;
//       }
//    }
//
//    public Boolean getWithLabel() {
//        return withLabel;
//    }
//
//    public void setWithLabel(Boolean withLabel) {
//        this.withLabel = withLabel;
//    }
//}
