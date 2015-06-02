package de.fzi.cep.sepa.commons.config;

public class ProaSenseConfig {

	public final static String zookeeperHost;
	public final static String kafkaHost;
	public final static int zookeeperPort;
	public final static int kafkaPort;
	
	static {
		//zookeeperHost = "kalmar39.fzi.de";
		zookeeperHost = "89.216.116.44";
		zookeeperPort = 2181;
		//kafkaHost = "kalmar39.fzi.de";
		kafkaHost = "89.216.116.44";
		kafkaPort = 9092;
	}
}
