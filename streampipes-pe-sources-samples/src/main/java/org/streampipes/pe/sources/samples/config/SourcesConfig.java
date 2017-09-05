package org.streampipes.pe.sources.samples.config;

import org.streampipes.config.SpConfig;

public enum SourcesConfig {
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
	private final static String SIMULATION_DELAY_MS = "simulation_delay_ms";
	private final static String SIMULATION_DELAY_NS = "simulation_delay_ns";
	private final static String SIMULATION_MAX_EVENTS = "simulation_max_events";
	private final static String SIMULATION_WAIT_EVERY = "simulation_wait_every";
	private final static String SIMULATION_WAIT_FOR = "simulation_wait_for";

	private final static String TWITTER_ACTIVE = "twitterActive";
	private final static String HELLA_REPLAY_ACTIVE = "hellaReplayActive";
	private final static String MHWIRTH_REPLAY_ACTIVE = "mhwirthReplayActive";
	private final static String TAXI_ACTIVE = "taxiActive";
	private final static String PROVE_IT_ACTIVE = "proveItActive";
	private final static String RANDOM_NUMBER_ACTIVE = "randomNumberActive";
	private final static String NISSATECH_ACTIVE = "nissatechRunning";

	private final static String ICON_HOST = "icon_host";
	private final static String ICON_PORT = "icon_port";

	public final static String serverUrl;
	public final static String iconBaseUrl;
	public final static String eventReplayURI;
	public final static String topicPrefixDdm;
	public final static String topicPrefixRam;

	SourcesConfig() {
		config = SpConfig.getSpConfig("pe/org.streampipes.pe.sources.samples");
		config.register(HOST, "pe-sources-samples", "Hostname for the pe sources samples");
		config.register(PORT, 8090, "Port for the pe sources samples");
		config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe sources samples project");
		config.register(KAFKA_PORT, 9092, "Port for kafka of the pe sources samples project");
		config.register(ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe sources samples project");
		config.register(ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe sources samples project");
		config.register(JMS_HOST, "activemq", "Hostname for pe sources samples service for active mq");
		config.register(JMS_PORT, 9092, "Port for pe sources samples service for active mq");
		config.register(SIMULATION_DELAY_MS,10, "Time for the simulation in milliseconds");
		config.register(SIMULATION_DELAY_NS, 0, "Time for the simulation in nanoseconds");
		config.register(SIMULATION_MAX_EVENTS,105000, "Amount of maximum events");
		config.register(SIMULATION_WAIT_EVERY, 1, "Time to wait interval");
		config.register(SIMULATION_WAIT_FOR, 800, "Time for the simulation to wait for in milliseconds");

		config.register(TWITTER_ACTIVE, false, "Source for twitter data");
		config.register(HELLA_REPLAY_ACTIVE, false, "Source for hella data");
		config.register(MHWIRTH_REPLAY_ACTIVE, false, "Source for mhwirth data");
		config.register(TAXI_ACTIVE, false, "Source for taxi data");
		config.register(PROVE_IT_ACTIVE, false, "Source for prove it data");
		config.register(RANDOM_NUMBER_ACTIVE, true, "Source for random data");
		config.register(NISSATECH_ACTIVE, false, "Source for nissatech data");

		config.register(ICON_HOST, "backend", "Hostname for the icon host");
		config.register(ICON_PORT, 80, "Port for the icons in nginx");
	}


	static {
		serverUrl = SourcesConfig.INSTANCE.getHost() + ":" + SourcesConfig.INSTANCE.getPort();
		iconBaseUrl = SourcesConfig.INSTANCE.getIconHost() + ":" + SourcesConfig.INSTANCE.getIconPort() +"/img";
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

	public String getJmsHost() {
		return config.getString(JMS_HOST);
	}

	public int getJmsPort() {
		return config.getInteger(JMS_PORT);
	}

	public int getSimulaitonDelayMs() {
		return config.getInteger(SIMULATION_DELAY_MS);
	}

	public int getSimulaitonDelayNs() {
		return config.getInteger(SIMULATION_DELAY_NS);
	}

	public int getMaxEvents() {
		return config.getInteger(SIMULATION_MAX_EVENTS);
	}

	public int getSimulationWaitEvery() {
		return config.getInteger(SIMULATION_WAIT_EVERY);
	}

	public int getSimulationWaitFor() {
		return config.getInteger(SIMULATION_WAIT_FOR);
	}


	public boolean isTwitterActive() {
		return config.getBoolean(TWITTER_ACTIVE);
	}

	public boolean isHellaActive() {
		return config.getBoolean(HELLA_REPLAY_ACTIVE);
	}

	public boolean isMhwirthActive() {
		return config.getBoolean(MHWIRTH_REPLAY_ACTIVE);
	}

	public boolean isTaxiActive() {
		return config.getBoolean(TAXI_ACTIVE);
	}

	public boolean isProveItActive() {
		return config.getBoolean(PROVE_IT_ACTIVE);
	}

	public boolean isRandomNumberActive() {
		return config.getBoolean(RANDOM_NUMBER_ACTIVE);
	}

	public boolean isNissatechActive() {
		return config.getBoolean(NISSATECH_ACTIVE);
	}

	public String getIconHost() {
		return config.getString(ICON_HOST);
	}

	public int getIconPort() {
		return config.getInteger(ICON_PORT);
	}

}
