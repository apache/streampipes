package de.fzi.cep.sepa.commons.config;

public enum BrokerConfig {

	LOCAL("localhost", 9092, "localhost", 2181, "tcp://localhost", 61616),
	KALMAR("kalmar39.fzi.de", 9092, "kalmar39.fzi.de", 2181, "tcp://localhost", 61616),
	NISSATECH_EXTERNAL("89.216.116.44", 9092, "89.216.116.44", 2181, "tcp://localhost", 61616),
	NISSATECH_INTERNAL("192.168.1.111", 9092, "192.168.1.111", 2181, "tcp://localhost", 61616),
	ALEKSANDAR_UNINOVA("10.172.67.55", 9092, "10.172.67.55", 2181, "tcp://localhost", 61616);
	
	private String kafkaHost;
	private int kafkaPort;
	private String zookeeperHost;
	private int zookeeperPort;
	private String jmsHost;
	private int jmsPort;
	
	BrokerConfig(String kafkaHost, int kafkaPort, String zookeeperHost, int zookeeperPort, String jmsHost, int jmsPort)
	{
		this.kafkaHost = kafkaHost;
		this.kafkaPort = kafkaPort;
		this.zookeeperHost = zookeeperHost;
		this.zookeeperPort = zookeeperPort;
		this.jmsHost = jmsHost;
		this.jmsPort = jmsPort;
	}
	
	
	public String getKafkaHost()
	{
		return kafkaHost;
	}
	
	public int getKafkaPort()
	{
		return kafkaPort;
	}
	
	public String getKafkaUrl()
	{
		return kafkaHost +":" +kafkaPort;
	}
	
	public int getZookeeperPort()
	{
		return zookeeperPort;
	}
	
	public String getZookeeperHost()
	{
		return zookeeperHost;
	}
	
	public String getZookeeperUrl()
	{
		return zookeeperHost + ":" +zookeeperPort;
	}
	
	public String getJmsHost()
	{
		return jmsHost;
	}
	
	public int getJmsPort()
	{
		return jmsPort;
	}
	
}
