package org.streampipes.model.grounding;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.KAFKA_TRANSPORT_PROTOCOL)
@Entity
public class KafkaTransportProtocol extends TransportProtocol {

	private static final long serialVersionUID = -4067982203807146257L;

	@RdfProperty(StreamPipes.ZOOKEEPER_HOST)
	private String zookeeperHost;
	
	@RdfProperty(StreamPipes.ZOOKEEPER_PORT)
	private int zookeeperPort;
	
	@RdfProperty(StreamPipes.KAFKA_PORT)
	private int kafkaPort;
	
	public KafkaTransportProtocol(String kafkaHost, int kafkaPort, String topic, String zookeeperHost, int zookeeperPort)
	{
		super(kafkaHost, topic);
		this.zookeeperHost = zookeeperHost;
		this.zookeeperPort = zookeeperPort;
		this.kafkaPort = kafkaPort;
	}
	
	public KafkaTransportProtocol(KafkaTransportProtocol other)
	{
		super(other);
		this.kafkaPort = other.getKafkaPort();
		this.zookeeperHost = other.getZookeeperHost();
		this.zookeeperPort = other.getZookeeperPort();
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

	public int getKafkaPort() {
		return kafkaPort;
	}

	public void setKafkaPort(int kafkaPort) {
		this.kafkaPort = kafkaPort;
	}

}
