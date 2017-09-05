package org.streampipes.pe.processors.esper.config;


import org.streampipes.config.SpConfig;

public enum EsperConfig {
	INSTANCE;

	private SpConfig config;
	private final static String HOST = "host";
	private final static String PORT = "port";

	private final static String ICON_HOST = "icon_host";
	private final static String ICON_PORT = "icon_port";

	public final static String serverUrl;
	public final static String iconBaseUrl;

	EsperConfig() {
		config = SpConfig.getSpConfig("pe/org.streampipes.pe.processors.esper");
		config.register(HOST, "pe-esper", "Hostname for the pe esper");
		config.register(PORT, 8090, "Port for the pe esper");

		config.register(ICON_HOST, "backend", "Hostname for the icon host");
		config.register(ICON_PORT, 80, "Port for the icons in nginx");
	}
	
	static {
		serverUrl = EsperConfig.INSTANCE.getHost() + ":" + EsperConfig.INSTANCE.getPort();
		iconBaseUrl = EsperConfig.INSTANCE.getIconHost() + ":" + EsperConfig.INSTANCE.getIconPort() +"/img";
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

	public String getIconHost() {
		return config.getString(ICON_HOST);
	}

	public int getIconPort() {
		return config.getInteger(ICON_PORT);
	}
}
