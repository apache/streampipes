package de.fzi.cep.sepa.flink.samples;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;

public class Config {

	public static final String JAR_FILE = "./semantic-builder-flink-samples-0.0.2-SNAPSHOT.jar";
//	public static final String JAR_FILE = "c:\\git\\semantic-builder-parent\\semantic-builder-flink-samples\\target\\semantic-builder-flink-samples-0.0.2-SNAPSHOT.jar";
//	public static final String JAR_FILE = "/Users/philippzehnder/Coding/fzi/semantic-builder-parent/semantic-builder-flink-samples/target/semantic-builder-flink-samples-0.0.2-SNAPSHOT.jar";
	
	public static final String FLINK_HOST = ClientConfiguration.INSTANCE.getFlinkHost();
	
	public static final int FLINK_PORT = ClientConfiguration.INSTANCE.getFlinkPort();

	public static final String iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/img";
}
