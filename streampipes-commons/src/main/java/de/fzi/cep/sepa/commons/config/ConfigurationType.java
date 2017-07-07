package de.fzi.cep.sepa.commons.config;

public enum ConfigurationType {
	SERVER(ConfigurationManager.getStreamPipesConfigFileLocation() + ConfigurationManager.getStreamPipesConfigFileLocation()), CLIENT(ConfigurationManager.getStreamPipesConfigFileLocation() +ConfigurationManager.getStreamPipesClientConfigFilename());
	
	private String configFile;
	
	ConfigurationType(String configFile)
	{
		this.configFile = configFile;
	}
	
	public String configFile()
	{
		return configFile;
	}
}
