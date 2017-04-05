package de.fzi.cep.sepa.axoom.hmi.config;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;

public class FlinkConfig {

	public static final String JAR_FILE = "./semantic-epa-sources-axoom-hmi.jar";

	public static final String FLINK_HOST = ClientConfiguration.INSTANCE.getFlinkHost();
	
	public static final int FLINK_PORT = ClientConfiguration.INSTANCE.getFlinkPort();

	public static final String iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/img";
}
