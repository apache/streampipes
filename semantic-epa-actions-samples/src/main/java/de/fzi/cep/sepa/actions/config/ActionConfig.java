package de.fzi.cep.sepa.actions.config;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;

public class ActionConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = ClientConfiguration.INSTANCE.getActionUrl();
		iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/img";
	}
}
