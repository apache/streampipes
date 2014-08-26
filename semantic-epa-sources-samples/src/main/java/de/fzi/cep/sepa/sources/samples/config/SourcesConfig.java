package de.fzi.cep.sepa.sources.samples.config;

public class SourcesConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	public final static String eventReplayURI;
	
	static {
		serverUrl = "http://localhost:8089";
		iconBaseUrl = "http://localhost:8080/semantic-epa-backend/img";
		eventReplayURI = "http://89.215.116.44:8084";
	}
}
