package de.fzi.cep.sepa.flink.samples;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;

public class Config {

	public static final String JAR_FILE = "c:\\git\\semantic-epa-parent\\semantic-epa-flink-samples\\target\\semantic-epa-flink-samples-0.0.2-SNAPSHOT.jar";
//	public static final String JAR_FILE = "/home/philipp/Coding/fzi/icep/semantic-epa-parent/semantic-epa-flink-samples/target/semantic-epa-flink-samples-0.0.1-SNAPSHOT.jar";
	
	public static final String FLINK_HOST = ClientConfiguration.INSTANCE.getFlinkHost();
	
	public static final int FLINK_PORT = ClientConfiguration.INSTANCE.getFlinkPort();
}
