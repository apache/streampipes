package de.fzi.cep.sepa.sources.samples.config;

import de.fzi.cep.sepa.commons.config.Configuration;

public class SourcesConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	public final static String eventReplayURI;
	public final static String topicPrefixDdm;
	public final static String topicPrefixRam;
	
	static {
		serverUrl = Configuration.SOURCES_BASE_URL;
		iconBaseUrl = Configuration.WEBAPP_BASE_URL +"/semantic-epa-backend/img";
		eventReplayURI = "http://89.216.116.44:8084";
		topicPrefixDdm = "SEPA.SEP.DDM.";
		topicPrefixRam = "SEPA.SEP.Ram";
	}
}
