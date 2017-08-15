package org.streampipes.pe.processors.standalone.config;

import org.streampipes.config.SpConfig;

public enum Config {
	INSTANCE;

	private SpConfig config;
	private final static String HOST = "host";
	private final static String PORT = "port";

	public final static String serverUrl;
	public final static String iconBaseUrl;

	Config() {
		config = SpConfig.getSpConfig("pe/org.streampipes.pe.processors.standalone");
		config.register(HOST, "standalone", "Hostname for the pe standalone processors");
		config.register(PORT, 8090, "Port for the pe standalone processors");
	}
	static {
		serverUrl = Config.INSTANCE.getHost() + ":" + Config.INSTANCE.getPort();
		iconBaseUrl = Config.INSTANCE.getHost() + ":" + Config.INSTANCE.getPort() +"/img";
	}

	public String getHost() {
		return config.getString(HOST);
	}

	public int getPort() {
		return config.getInteger(PORT);
	}

}
