package org.streampipes.pe.sinks.standalone.config;

import org.streampipes.commons.config.ClientConfiguration;

public class ActionConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = ClientConfiguration.INSTANCE.getActionUrl();
		iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/img";
	}

	public static final String getIconUrl(String pictureName) {
		return iconBaseUrl +"/" +pictureName +".png";
	}
}
