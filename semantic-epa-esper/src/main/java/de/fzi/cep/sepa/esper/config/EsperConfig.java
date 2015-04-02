package de.fzi.cep.sepa.esper.config;

import de.fzi.cep.sepa.commons.Configuration;

public class EsperConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = Configuration.ESPER_BASE_URL;
		iconBaseUrl = Configuration.WEBAPP_BASE_URL +"/semantic-epa-backend/img";
	}
}
