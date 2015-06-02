package de.fzi.cep.sepa.commons.config;

public class SampleConfig {

	public final static String zookeeperHost;
	public final static String kafkaHost;
	public final static int zookeeperPort;
	public final static int kafkaPort;
	
	public final static String jmsHost;
	public final static int jmsPort;
	
	static {
		zookeeperHost = "kalmar39.fzi.de";
		zookeeperPort = 2181;
		kafkaHost = "kalmar39.fzi.de";
		kafkaPort = 8090;
		jmsHost = "tcp://localhost";
		jmsPort = 61616;
	}
	
}
