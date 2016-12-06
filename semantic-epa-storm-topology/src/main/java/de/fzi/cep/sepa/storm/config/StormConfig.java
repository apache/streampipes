package de.fzi.cep.sepa.storm.config;

/**
 * Created by robin on 16.10.14.
 */
public class StormConfig {
    public final static String serverUrl;
    public final static int port;
    public final static String iconBaseUrl;
	

    static {
        serverUrl = "http://localhost:8093/";
        port = 8090;
        iconBaseUrl = "http://localhost:8080/semantic-builder-backend/img";
    }
}
