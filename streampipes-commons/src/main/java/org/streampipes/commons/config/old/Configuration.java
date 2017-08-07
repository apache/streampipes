package org.streampipes.commons.config.old;

import org.streampipes.commons.Utils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;

import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * Configuration objects containing loads all properties from a file
 * and holds runtime changes.
 */
public class Configuration {

	
	private static Configuration instance;
	private PropertiesConfiguration config;
	
	public RDFFormat RDF_FORMAT = RDFFormat.JSONLD;
	
	private String HOSTNAME;
	public  String SERVER_URL;
	
	public String CONTEXT_PATH = "/semantic-epa-backend";
	
	public String KAFKA_HOST;
	public String KAFKA_PROTOCOL;
	public int KAFKA_PORT;
	
	public String JMS_HOST;
	public String JMS_PROTOCOL;
	public int JMS_PORT;
	
	public String TCP_SERVER_URL;
	public int TCP_SERVER_PORT;
	
	public int WEBAPP_PORT;
	public String WEBAPP_BASE_URL;
	
	public String ZOOKEEPER_HOST;
	public int ZOOKEEPER_PORT;
	public String ZOOKEEPER_PROTOCOL;
	
	public String APP_CONFIG;

	
	public String MARKETPLACE_URL;
	public List<String> POD_URLS;
	
	/**
	 * Constructor loads config data from config file.
	 */
	private Configuration() {
		
			if (ConfigurationManager.isConfigured())
			{
				Logger.getAnonymousLogger().info("Loading config file...");
				try {
					config = new PropertiesConfiguration(ConfigurationManager.getStreamPipesConfigFullPath());
					
					HOSTNAME = config.getString("hostname");
					SERVER_URL = config.getString("server_url");
					TCP_SERVER_URL = config.getString("tcp_server_url");
					TCP_SERVER_PORT = config.getInt("tcp_server_port")	;
					WEBAPP_PORT = config.getInt("webapp_port");
					WEBAPP_BASE_URL = SERVER_URL + ":" + WEBAPP_PORT;

					CONTEXT_PATH = config.getString("context_path");
					RDF_FORMAT = RDF_FORMAT.JSONLD;
					
					KAFKA_HOST = config.getString("kafkaHost");
					KAFKA_PROTOCOL = config.getString("kafkaProtocol");
					KAFKA_PORT = config.getInt("kafkaPort");
					
					JMS_HOST  = config.getString("jmsHost");;
					JMS_PROTOCOL = config.getString("jmsProtocol");
					JMS_PORT = config.getInt("jmsPort");
					
					ZOOKEEPER_HOST  = config.getString("zookeeperHost");
					ZOOKEEPER_PROTOCOL = config.getString("zookeeperProtocol");
					ZOOKEEPER_PORT = config.getInt("zookeeperPort");

				} catch (Exception e) {
					e.printStackTrace();
					loadDefaults();
				} 
		}
		else {
			Logger.getAnonymousLogger().info("Loading defaults..");
			loadDefaults();
		}
	}
	
	private void loadDefaults() {
		// load defaults
					HOSTNAME =Utils.getHostname();
					SERVER_URL = "http://" +HOSTNAME;
					
					CONTEXT_PATH = "/semantic-epa-backend";
					
					KAFKA_HOST = Utils.getHostname();
					KAFKA_PROTOCOL = "http";
					KAFKA_PORT = 9092;
					
					JMS_HOST = Utils.getHostname();
					JMS_PROTOCOL = "tcp";
					JMS_PORT = 61616;
					
					TCP_SERVER_URL = "tcp://" +HOSTNAME;
					TCP_SERVER_PORT = 61616;
					
					WEBAPP_PORT = 8080;
					WEBAPP_BASE_URL = SERVER_URL + ":" + WEBAPP_PORT;
					
					ZOOKEEPER_HOST = Utils.getHostname();
					ZOOKEEPER_PORT = 2181;
					ZOOKEEPER_PROTOCOL = "http";
					
					RDF_FORMAT = RDF_FORMAT.JSONLD;
	}

	public static Configuration getInstance() {
		if (null == instance) {
			instance = new Configuration();
			return instance;
		}
		return instance;
	}
	
//	public String getJmsAddress()
//	{
//		return JMS_PROTOCOL +"://" +JMS_HOST +":" +JMS_PORT;
//	}
//
//	public String getHostname() {
//		InetAddress addr;
//		try {
//			addr = InetAddress.getLocalHost();
//			return Protocol.getProtocol("http").getScheme()  + "://" +addr.getCanonicalHostName() +":";
//		} catch (UnknownHostException e) {
//			return "http://localhost:";
//		}
//	}
	
	public static void update() {
		instance = new Configuration();
	}

	public PropertiesConfiguration getConfig() {
		return config;
	}


	// Default values if something can't be read from the config file
	


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

	public final BrokerConfig getBrokerConfig()
	{
		return BrokerConfig.CONFIGURED;
	}

	public final boolean isDemoMode()
	{
		return false;
	}

}
