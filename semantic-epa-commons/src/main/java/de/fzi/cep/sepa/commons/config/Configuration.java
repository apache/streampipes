package de.fzi.cep.sepa.commons.config;

import java.io.*;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;
import org.slf4j.Logger;

/**
 * Configuration objects containing loads all properties from a file
 * and holds runtime changes.
 */
public class Configuration {

	private static Configuration instance;
	private PropertiesConfiguration config;
	/**
	 * Constructor loads config data from config file.
	 */
	private Configuration() {
		try {
			config = new PropertiesConfiguration("streampipe.properties");
			HOSTNAME = config.getString("hostname");
			SERVER_URL = config.getString("server_url");
			TCP_SERVER_URL = config.getString("tcp_server_url");
			TCP_SERVER_PORT = config.getInt("tcp_server_port")	;
			ACTION_PORT = config.getInt("action_port");
			ESPER_PORT = config.getInt("esper_port");
			ALGORITHM_PORT = config.getInt("algorithm_port");
			SOURCES_PORT = config.getInt("sources_port");
			WEBAPP_PORT = config.getInt("webapp_port");
			ALGORITHM_BASE_URL = SERVER_URL +":" +ALGORITHM_PORT;
			ESPER_BASE_URL = SERVER_URL + ":" + ESPER_PORT;
			ACTION_BASE_URL = SERVER_URL + ":" + ACTION_PORT;
			SOURCES_BASE_URL = SERVER_URL + ":" + SOURCES_PORT;
			WEBAPP_BASE_URL = SERVER_URL + ":" + WEBAPP_PORT;
			SESAME_URI = config.getString("sesame_uri");
			SESAME_REPOSITORY_ID = config.getString("sesame_repository_id");
			CONTEXT_PATH = config.getString("context_path");
			RDF_FORMAT = RDF_FORMAT.JSONLD;

			config.addProperty("test_key", "new url value");
			//config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static Configuration getInstance() {
		if (null == instance) {
			instance = new Configuration();
			return instance;
		}
		return instance;
	}

	public PropertiesConfiguration getConfig() {
		return config;
	}


	// Default values if something can't be read from the config file
	private static  String HOSTNAME ="localhost";

	public  String SERVER_URL = "http://" +HOSTNAME;

	public  String TCP_SERVER_URL = "tcp://" +HOSTNAME;

	public int TCP_SERVER_PORT = 61616;

	public int ACTION_PORT = 8091;

	public int ESPER_PORT = 8090;

	public int ALGORITHM_PORT = 8092;

	public int SOURCES_PORT = 8089;

	public int WEBAPP_PORT = 8080;

	public String ALGORITHM_BASE_URL = SERVER_URL +":" +ALGORITHM_PORT;

	public String ESPER_BASE_URL = SERVER_URL + ":" + ESPER_PORT;

	public String ACTION_BASE_URL = SERVER_URL + ":" + ACTION_PORT;

	public  String SOURCES_BASE_URL = SERVER_URL + ":" + SOURCES_PORT;

	public String WEBAPP_BASE_URL = SERVER_URL + ":" + WEBAPP_PORT;

	public String SESAME_URI = "http://localhost:8080/openrdf-sesame";

	public String SESAME_REPOSITORY_ID = "test-6";

	public String CONTEXT_PATH = "/semantic-epa-backend";

	public RDFFormat RDF_FORMAT = RDFFormat.JSONLD;

	public RDFWriter getRioWriter(OutputStream stream) throws RDFHandlerException
	{
		RDFWriter writer = Rio.createWriter(RDF_FORMAT, stream);

		writer.handleNamespace("sepa", "http://sepa.event-processing.org/sepa#");
		writer.handleNamespace("ssn", "http://purl.oclc.org/NET/ssnx/ssn#");
		writer.handleNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
		writer.handleNamespace("empire", "urn:clarkparsia.com:empire:");
		writer.handleNamespace("fzi", "urn:fzi.de:sepa:");

		writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
		writer.getWriterConfig().set(JSONLDSettings.OPTIMIZE, true);

		return writer;
	}

	public static final BrokerConfig getBrokerConfig()
	{
		return BrokerConfig.KALMAR;
	}

	public final boolean isDemoMode()
	{
		return true;
	}

}
