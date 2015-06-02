package de.fzi.cep.sepa.commons.config;

public class ProaSenseConfigNissatech {
	
	public final static String zookeeperHost;
	public final static String kafkaHost;
	public final static int zookeeperPort;
	public final static int kafkaPort;
	
	static {
		zookeeperHost = "89.216.116.44";
		zookeeperPort = 2181;
		kafkaHost = "89.216.116.44";
		kafkaPort = 8090;
	}
}
