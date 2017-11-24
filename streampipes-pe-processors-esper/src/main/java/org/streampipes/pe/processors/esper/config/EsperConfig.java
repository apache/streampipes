package org.streampipes.pe.processors.esper.config;


import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum EsperConfig implements PeConfig {
	INSTANCE;

	private SpConfig config;
	private final static String HOST = "host";
	private final static String PORT = "port";

	private final static String ICON_HOST = "icon_host";
	private final static String ICON_PORT = "icon_port";

	public final static String serverUrl;
	public final static String iconBaseUrl;

	private final static String SERVICE_ID = "pe/org.streampipes.pe.processors.esper";
	private final static String SERVICE_NAME = "service_name";

	EsperConfig() {
		config = SpConfig.getSpConfig(SERVICE_ID);
		config.register(HOST, "pe-esper", "Hostname for the pe esper");
		config.register(PORT, 8090, "Port for the pe esper");

		config.register(ICON_HOST, "backend", "Hostname for the icon host");
		config.register(ICON_PORT, 80, "Port for the icons in nginx");

		config.register(SERVICE_NAME, "Processor esper", "The name of the service");

	}
	
	static {
		serverUrl = EsperConfig.INSTANCE.getHost() + ":" + EsperConfig.INSTANCE.getPort();
		iconBaseUrl = EsperConfig.INSTANCE.getIconHost() + ":" + EsperConfig.INSTANCE.getIconPort() +"/img/pe_icons";
	}

	public static final String getIconUrl(String pictureName) {
		return iconBaseUrl +"/" +pictureName +".png";
	}

	@Override
	public String getHost() {
		return config.getString(HOST);
	}

	@Override
	public int getPort() {
		return config.getInteger(PORT);
	}

	public String getIconHost() {
		return config.getString(ICON_HOST);
	}

	public int getIconPort() {
		return config.getInteger(ICON_PORT);
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
