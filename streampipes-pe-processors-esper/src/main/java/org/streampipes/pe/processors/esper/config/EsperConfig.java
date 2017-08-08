package org.streampipes.pe.processors.esper.config;


import org.streampipes.config.SpConfig;

public enum EsperConfig {
	INSTANCE;

	private SpConfig config;
	private final static String HOST = "host";
	private final static String PORT = "port";

	public final static String serverUrl;
	public final static String iconBaseUrl;

	EsperConfig() {
		config = SpConfig.getSpConfig("pe/esper");
		config.register(HOST, "esper", "Hostname for the pe esper");
		config.register(PORT, 8090, "Port for the pe esper");
	}
	
	static {
		serverUrl = EsperConfig.INSTANCE.getHost() + ":" + EsperConfig.INSTANCE.getPort();
		iconBaseUrl = EsperConfig.INSTANCE.getHost() + ":" + EsperConfig.INSTANCE.getPort() +"/img";
	}

	public static final String getIconUrl(String pictureName) {
		return iconBaseUrl +"/" +pictureName +".png";
	}
	
	public String getHost() {
		return config.getString(HOST);
	}

	public int getPort() {
		return config.getInteger(PORT);
	}

}
