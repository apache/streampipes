package de.fzi.cep.sepa.commons.config;

public class ProaSenseConfig {

	public final static String zookeeperHost;
	public final static String kafkaHost;
	public final static int zookeeperPort;
	public final static int kafkaPort;
	
	public final static String kafkaUrl;
	
	static {
		//zookeeperHost = "kalmar39.fzi.de";
		//zookeeperHost = "89.216.116.44";
		zookeeperHost = "192.168.1.111";;
		zookeeperPort = 2181;
		//kafkaHost = "kalmar39.fzi.de";
		kafkaHost = "192.168.1.111";
		kafkaPort = 9092;
		
		kafkaUrl = kafkaHost + ":" +kafkaPort;
	}
}
