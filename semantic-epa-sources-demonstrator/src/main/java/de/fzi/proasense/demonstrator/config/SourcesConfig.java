package de.fzi.proasense.demonstrator.config;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;

public class SourcesConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	public final static String eventReplayURI;
	public final static String topicPrefixDdm;
	public final static String topicPrefixRam;
	
	static {
		serverUrl = ClientConfiguration.INSTANCE.getSourcesUrl();
		iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/img";
		eventReplayURI = "http://89.216.116.44:8084";
		topicPrefixDdm = "SEPA.SEP.DDM.";
		topicPrefixRam = "SEPA.SEP.Ram";
	}
}
