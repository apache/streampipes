package de.fzi.proasense.hella.config;

import de.fzi.cep.sepa.commons.config.Configuration;

public class SourcesConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	public final static String topicPrefixDdm;
	public final static String topicPrefixRam;
	
	static {
		serverUrl = Configuration.getInstance().SOURCES_BASE_URL;
		iconBaseUrl = Configuration.getInstance().WEBAPP_BASE_URL +"/semantic-epa-backend/img";
		topicPrefixDdm = "SEPA.SEP.DDM.";
		topicPrefixRam = "SEPA.SEP.Ram";
	}
}
