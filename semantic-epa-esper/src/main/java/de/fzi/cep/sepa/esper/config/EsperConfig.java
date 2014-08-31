package de.fzi.cep.sepa.esper.config;

public class EsperConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = "http://localhost:8090";
		iconBaseUrl = "http://anemone06.fzi.de/semantic-epa-backend/img";
	}
}
