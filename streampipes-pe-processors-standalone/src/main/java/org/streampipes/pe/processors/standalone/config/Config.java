package org.streampipes.pe.processors.standalone.config;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum Config implements PeConfig {
	INSTANCE;

	private SpConfig config;
	private final static String HOST = "host";
	private final static String PORT = "port";

	public final static String serverUrl;
	public final static String iconBaseUrl;

	private final static String SERVICE_ID= "pe/org.streampipes.pe.processors.standalone";
	private final static String SERVICE_NAME = "service_name";

	Config() {
		config = SpConfig.getSpConfig(SERVICE_ID);
		config.register(HOST, "standalone", "Hostname for the pe standalone processors");
		config.register(PORT, 8090, "Port for the pe standalone processors");

		config.register(SERVICE_NAME, "Processors standalone", "The name of the service");


	}
	static {
		serverUrl = Config.INSTANCE.getHost() + ":" + Config.INSTANCE.getPort();
		iconBaseUrl = Config.INSTANCE.getHost() + ":" + Config.INSTANCE.getPort() +"/img/pe_icons";
	}

	public String getHost() {
		return config.getString(HOST);
	}

	public int getPort() {
		return config.getInteger(PORT);
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
