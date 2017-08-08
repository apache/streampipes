package org.streampipes.wrapper.flink.samples;

import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.config.SpConfig;

public enum FlinkConfig {
	INSTANCE;

	private SpConfig config;
	public static final String JAR_FILE = "./streampipes-pe-mixed-flink.jar";
//	public static final String JAR_FILE = "c:\\git\\semantic-epa-parent\\semantic-epa-flink-samples\\target\\semantic-epa-flink-samples-0.40.3-SNAPSHOT.jar";
//	public static final String JAR_FILE = "/Users/philippzehnder/Coding/fzi/semantic-epa-parent/semantic-epa-flink-samples/target/semantic-epa-flink-samples-0.40.3-SNAPSHOT.jar";

    private final static String FLINK_HOST = "flink_host";
	private final static String FLINK_PORT = "flink_port";

	FlinkConfig() {
	    config = SpConfig.getSpConfig("pe/flink-mixed");

       	config.register(FLINK_HOST, "flink", "Host for the flink cluster");
        config.register(FLINK_PORT, 6123, "Port for the flink cluster");

	}

	
//	public static final String FLINK_HOST = ClientConfiguration.INSTANCE.getFlinkHost();
	
//	public static final int FLINK_PORT = ClientConfiguration.INSTANCE.getFlinkPort();

	public static final String iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/img";

	public static final String getIconUrl(String pictureName) {
		return iconBaseUrl +"/" +pictureName +".png";
	}
}
