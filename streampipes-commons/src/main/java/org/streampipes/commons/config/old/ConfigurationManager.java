package org.streampipes.commons.config.old;

import org.streampipes.commons.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ConfigurationManager {

	public static boolean isConfigured() {
		return new File(getStreamPipesConfigFileLocation()).exists() && new File(getStreamPipesConfigFullPath()).exists();
	}
	
	public static boolean isClientConfigured() {
		return new File(getStreamPipesClientConfigFullPath()).exists();
	}
	
	public static String getStreamPipesConfigFullPath()
	{
		return getStreamPipesConfigFileLocation() + getStreamPipesConfigFilename();
	}

	public static File getConfigFile(String filename) throws IOException {
		File file = new File(getStreamPipesConfigFileLocation() +filename);
		if (!file.exists()) {
			throw new IOException("Could not find config file " +filename);
		}
		else {
			return file;
		}
	}
	
	public static String getStreamPipesClientConfigFullPath()
	{
		return getStreamPipesConfigFileLocation() + getStreamPipesClientConfigFilename();
	}
	
	public static String getStreamPipesConfigFileLocation()
	{
		return System.getProperty("user.home") + File.separator +".streampipes" +File.separator;
	}
	
	public static String getStreamPipesConfigFilename() 
	{
		return "streampipes-server.config";
	}
	
	public static String getStreamPipesClientConfigFilename()
	{
		return "streampipes-client.config";
	}

	public static WebappConfigurationSettings getWebappConfigurationFromProperties() {
		Configuration cfg = Configuration.getInstance();
		WebappConfigurationSettings settings = new WebappConfigurationSettings();
		settings.setJmsHost(cfg.JMS_HOST);
		settings.setJmsPort(cfg.JMS_PORT);
		settings.setJmsProtocol(cfg.JMS_PROTOCOL);
		settings.setKafkaHost(cfg.KAFKA_HOST);
		settings.setKafkaPort(cfg.KAFKA_PORT);
		settings.setKafkaProtocol(cfg.KAFKA_PROTOCOL);
		settings.setZookeeperHost(cfg.ZOOKEEPER_HOST);
		settings.setZookeeperPort(cfg.ZOOKEEPER_PORT);
		settings.setZookeeperProtocol(cfg.ZOOKEEPER_PROTOCOL);

		return settings;
	}
	
	public static void storeWebappConfigurationToProperties(File file, File pathToFile, WebappConfigurationSettings settings) throws IOException {
		
		Properties properties = new Properties() {
		    @Override
		    public synchronized Enumeration<Object> keys() {
		        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
		    }
		};
		
		properties.put("kafkaProtocol", settings.getKafkaProtocol());
		properties.put("kafkaHost", settings.getKafkaHost());
		properties.put("kafkaPort", String.valueOf(settings.getKafkaPort()));
		
		properties.put("zookeeperProtocol", settings.getZookeeperProtocol());
		properties.put("zookeeperHost", settings.getZookeeperHost());
		properties.put("zookeeperPort", String.valueOf(settings.getZookeeperPort()));
		
		properties.put("jmsProtocol", settings.getJmsProtocol());
		properties.put("jmsHost", settings.getJmsHost());
		properties.put("jmsPort", String.valueOf(settings.getJmsPort()));
		
		properties.put("sesameUrl", settings.getSesameUrl());
		properties.put("sesameDbName", settings.getSesameDbName());

		properties.put("hostname", Utils.getHostname());
		properties.put("server_url", "http://" +Utils.getHostname());
		properties.put("tcp_server_url", "tcp://" +Utils.getHostname());
		properties.put("tcp_server_port", "61616");
		properties.put("action_port", "8091");
		properties.put("esper_port", "8090");
		properties.put("algorithm_port", "8092");
		properties.put("sources_port", "8089");
		properties.put("webapp_port", "8030");
		properties.put("context_path", "/semantic-epa-backend");
		

		if (!pathToFile.exists()) pathToFile.mkdir();
		if (!file.exists()) file.createNewFile();
	
		properties.store(new FileWriter(file), "");
		Configuration.update();
	}

	private static Object toField(List<String> podUrls) {
		String result = "";
		for(int i = 0; i < podUrls.size(); i++)  {
			result = result +podUrls.get(i);
			if (i != (podUrls.size() - 1)) result = result +",";
		}
		return result;
	}

}
