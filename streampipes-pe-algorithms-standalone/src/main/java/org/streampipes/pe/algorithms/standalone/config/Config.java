package org.streampipes.pe.algorithms.standalone.config;

import org.streampipes.commons.config.ClientConfiguration;

public class Config {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = ClientConfiguration.INSTANCE.getAlgorithmUrl();
		iconBaseUrl = ClientConfiguration.INSTANCE.getWebappUrl() +"/semantic-epa-backend/img";
	}
	
}
