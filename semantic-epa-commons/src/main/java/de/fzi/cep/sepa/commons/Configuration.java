package de.fzi.cep.sepa.commons;

public class Configuration {

	public static final String SERVER_URL = "http://localhost";
	
	public static final String TCP_SERVER_URL = "tcp://localhost";
	
	public static final int ACTION_PORT = 8091;
	
	public static final int ESPER_PORT = 8090;
	
	public static final int SOURCES_PORT = 8089;
	
	public static final int WEBAPP_PORT = 8080;
	
	public static final String ESPER_BASE_URL = SERVER_URL + ":" + ESPER_PORT;
	
	public static final String ACTION_BASE_URL = SERVER_URL + ":" + ACTION_PORT;
	
	public static final String SOURCES_BASE_URL = SERVER_URL + ":" + SOURCES_PORT;
	
	public static final String WEBAPP_BASE_URL = SERVER_URL + ":" + WEBAPP_PORT;
	
	//public static final String EMPIRE_CONFIG_LOCATION = "/home/riemer/empire.config.properties";

	public static final String EMPIRE_CONFIG_LOCATION = "c:\\workspace\\semantic-epa-parent\\semantic-epa-backend\\semantic-epa-storage\\src\\main\\resources\\empire.config.properties";

	//public static final String EMPIRE_CONFIG_LOCATION = "e:\\Workspace Eclipse Luna\\semantic-epa-parent\\semantic-epa-backend\\semantic-epa-storage\\src\\main\\resources\\empire.config.properties";

}
