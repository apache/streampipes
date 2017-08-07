package org.streampipes.commons.config.old;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.rio.RDFFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public enum ClientConfiguration {

	INSTANCE;
	
	private String hostname;
	
	private int esperPort;
	private int algorithmPort;
	private int sourcesPort;
	
	private String zookeeperHost;
	private int zookeeperPort;
	
	private String kafkaHost;
	private int kafkaPort;
	
	private String jmsHost;
	private int jmsPort;
	
	private String kafkaUrl;
	private String zookeeperUrl;

	private String esperUrl;
	private String algorithmUrl;
	private String sourcesUrl;
	private String jmsUrl;
	
	private String webappHost;
	private int webappPort;
	private String webappUrl;


	private String iconUrl;
	private String iconHost;
	private int iconPort;
	private String iconScheme;
	
	private File pathToFile;
	private File file;
	
	private boolean twitterActive;
	private boolean randomNumberActive;
	private boolean taxiActive;
	private boolean hellaReplayActive;
	private boolean mhwirthReplayActive;
	private boolean proveItActive;
	
	private boolean nissatechRunning;
	
	private long simulationMaxEvents;
	private long simulationDelayMs;
	private int simulationDelayNs;
	private long waitEvery;
	private long waitForMs;
	
	private String flinkHost;
	private int flinkPort;
	
	private String elasticsearchHost;
	private int elasticsearchPort;

	private String datalocation;

	private PropertiesConfiguration config;
	
	ClientConfiguration()
	{
		pathToFile = new File(ConfigurationManager.getStreamPipesConfigFileLocation());
		file = new File(ConfigurationManager.getStreamPipesClientConfigFullPath());
		 
		if (ConfigurationManager.isClientConfigured())
			loadPropertySettings();
		else
			createDefaultSettings();
	}

	private void createDefaultSettings() {
			Properties properties = new Properties() {
			    @Override
			    public synchronized Enumeration<Object> keys() {
			        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			    }
			};
			
			properties.put("hostname", "localhost");
			properties.put("esper_port", "8090");
			properties.put("sources_port", "8089");
			properties.put("algorithm_port", "8093");
			
			properties.put("kafkaHost", "localhost");
			properties.put("kafkaPort", "9092");
			
			properties.put("jmsHost", "tcp://localhost");
			properties.put("jmsPort", "61616");
		
			properties.put("zookeeperHost", "localhost");
			properties.put("zookeeperPort", "2181");
			
			properties.put("zookeeperPort", "2181");

			properties.put("couchDbHost", "localhost");
			properties.put("couchDbPort", "5984");

			properties.put("webappHost", "localhost");
			properties.put("webappPort", "8080");
			
			properties.put("twitterActive", false);
			properties.put("randomNumberActive", false);
			properties.put("taxiActive", false);
			properties.put("hellaReplayActive", true);
			properties.put("mhwirthReplayActive", false);
			properties.put("proveItActive", false);
			
			properties.put("nissatechRunning", false);
			properties.put("iconHost", "localhost");
			properties.put("iconPort", 8080);
			properties.put("iconScheme", "http");
			
			properties.put("simulationMaxEvents", 10000);
			properties.put("simulationDelayMs", 0);
			properties.put("simulationDelayNs", 0);
			properties.put("waitEvery", 0);
			properties.put("waitForMs", 0);
			
			properties.put("kafkaLingerMs", 0);
			properties.put("kafkaBatchSize", 0);
			properties.put("kafkaAcks", "1");
			
			properties.put("flinkHost", "ipe-koi05.fzi.de");
			properties.put("flinkPort", 6123);
			
			properties.put("elasticsearchHost", "ipe-koi05.fzi.de");
			properties.put("elasticsearchPort", 9300);

			properties.put("podHostname", "localhost");
			properties.put("podPort", 8081);
			properties.put("podDeploymentDirectory", "/opt/felix/dropin");

			
			properties.put("streamStoryUrl", "");
			
			
			if (!pathToFile.exists()) pathToFile.mkdir();
			if (!file.exists())
				try {
					file.createNewFile();
					properties.store(new FileWriter(file), "");
					loadPropertySettings();
				} catch (IOException e) {
					e.printStackTrace();
				}
	}

	private void loadPropertySettings() {
		try {
			config = new PropertiesConfiguration(ConfigurationManager.getStreamPipesClientConfigFullPath());
			
			this.hostname = config.getString("hostname");
			this.esperPort = config.getInt("esper_port");
			this.algorithmPort = config.getInt("algorithm_port");
			this.sourcesPort = config.getInt("sources_port");
			
			this.algorithmUrl = hostname +":" +algorithmPort;
			this.esperUrl = hostname + ":" + esperPort;
			this.sourcesUrl = hostname + ":" + sourcesPort;
			
			this.kafkaHost = config.getString("kafkaHost");
			this.kafkaPort = config.getInt("kafkaPort");
			this.kafkaUrl = kafkaHost + ":" +kafkaPort;
			
			this.jmsHost  = config.getString("jmsHost");;
			this.jmsPort = config.getInt("jmsPort");
			this.jmsUrl =  jmsHost +":" +jmsPort;
			
			this.zookeeperHost  = config.getString("zookeeperHost");
			this.zookeeperPort = config.getInt("zookeeperPort");
			this.zookeeperUrl = zookeeperHost +":" +zookeeperPort;
			
			this.webappHost = config.getString("webappHost");
			this.webappPort = config.getInt("webappPort");
			this.webappUrl = "http://" +webappHost +":" +webappPort;
			
			this.twitterActive = config.getBoolean("twitterActive");
			this.randomNumberActive = config.getBoolean("randomNumberActive");
			this.taxiActive = config.getBoolean("taxiActive");
			this.hellaReplayActive = config.getBoolean("hellaReplayActive");
			this.mhwirthReplayActive = config.getBoolean("mhwirthReplayActive");
			this.proveItActive = config.getBoolean("proveItActive");

			this.nissatechRunning = config.getBoolean("nissatechRunning");
			
			this.simulationMaxEvents = config.getLong("simulationMaxEvents");
			this.simulationDelayMs = config.getLong("simulationDelayMs");
			this.simulationDelayNs = config.getInt("simulationDelayNs");
			
			this.waitEvery = config.getLong("waitEvery");
			this.waitForMs = config.getLong("waitForMs");
			
			this.flinkHost = config.getString("flinkHost");
			this.flinkPort = config.getInt("flinkPort");
			
			this.iconHost = config.getString("iconHost");
			this.iconPort = config.getInt("iconPort");
			this.iconScheme = config.getString("iconScheme");
			this.iconUrl =  iconScheme + "://" +iconHost +":" +iconPort;
			
			this.elasticsearchPort = config.getInt("elasticsearchPort");
			this.elasticsearchHost = config.getString("elasticsearchHost");
			
			this.datalocation = config.getString("datalocation");

			if (iconScheme.equals("https")) this.iconUrl = iconScheme +"://" +iconHost;
			
			
		} catch (ConfigurationException e) {
			System.out.println(e);
			createDefaultSettings();
		}
		
	}

	public String getHostname() {
		return hostname;
	}

	public String getZookeeperHost() {
		return zookeeperHost;
	}

	public int getZookeeperPort() {
		return zookeeperPort;
	}

	public String getKafkaHost() {
		return kafkaHost;
	}

	public int getKafkaPort() {
		return kafkaPort;
	}

	public String getJmsHost() {
		return jmsHost;
	}

	public int getJmsPort() {
		return jmsPort;
	}

	public String getKafkaUrl() {
		return kafkaUrl;
	}

	public String getZookeeperUrl() {
		return zookeeperUrl;
	}

	public String getEsperUrl() {
		return esperUrl;
	}

	public String getAlgorithmUrl() {
		return algorithmUrl;
	}

	public String getSourcesUrl() {
		return sourcesUrl;
	}

	public String getJmsUrl() {
		return jmsUrl;
	}
		
	public String getWebappHost() {
		return webappHost;
	}

	public int getWebappPort() {
		return webappPort;
	}

	public String getWebappUrl() {
		return webappUrl;
	}
	
	public boolean isTwitterActive() {
		return twitterActive;
	}

	public boolean isRandomNumberActive() {
		return randomNumberActive;
	}

	public boolean isTaxiActive() {
		return taxiActive;
	}

	public boolean isHellaReplayActive() {
		return hellaReplayActive;
	}

	public boolean isMhwirthReplayActive() {
		return mhwirthReplayActive;
	}

	public boolean isProveItActive() {
		return proveItActive;
	}
	
	public boolean isNissatechRunning() {
		return nissatechRunning;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public long getSimulationMaxEvents() {
		return simulationMaxEvents;
	}
	
	public long getSimulationDelayMs() {
		return simulationDelayMs;
	}
	
	public int getSimulationDelayNs() {
		return simulationDelayNs;
	}
	
	public long getWaitEvery() {
		return waitEvery;
	}
	
	public long getWaitForMs() {
		return waitForMs;
	}

	
	public String getFlinkHost() {
		return flinkHost;
	}

	public int getFlinkPort() {
		return flinkPort;
	}

	public String getElasticsearchHost() {
		return elasticsearchHost;
	}

	public int getElasticsearchPort() {
		return elasticsearchPort;
	}


	public String getDatalocation() {
		return datalocation;
	}

}
