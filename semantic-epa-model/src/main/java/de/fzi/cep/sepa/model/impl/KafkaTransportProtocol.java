package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:KafkaTransportProtocol")
@Entity
public class KafkaTransportProtocol extends TransportProtocol {

	@RdfProperty("sepa:zookeeperHost")
	private String zookeeperHost;
	
	@RdfProperty("sepa:zookeeperPort")
	private int zookeeperPort;
	
	public KafkaTransportProtocol(String kafkaHost, int kafkaPort, String topic, String zookeeperHost, int zookeeperPort)
	{
		super(kafkaHost, kafkaPort, topic);
		this.zookeeperHost = zookeeperHost;
		this.zookeeperPort = zookeeperPort;
	}
	
	public KafkaTransportProtocol()
	{
		super();
	}

	public String getZookeeperHost() {
		return zookeeperHost;
	}

	public void setZookeeperHost(String zookeeperHost) {
		this.zookeeperHost = zookeeperHost;
	}

	public int getZookeeperPort() {
		return zookeeperPort;
	}

	public void setZookeeperPort(int zookeeperPort) {
		this.zookeeperPort = zookeeperPort;
	}
	
}
