package de.fzi.cep.sepa.esper;

import org.apache.commons.lang.RandomStringUtils;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.CamelConfig.Kafka;

public class GroundingConfig {

	private EventGrounding grounding;
	private CamelConfig camelConfig;
	private String endpointUri;
	private String brokerAlias;
	
	public GroundingConfig(EventGrounding grounding)
	{
		this.grounding = grounding;
		this.brokerAlias = RandomStringUtils.randomAlphabetic(10);
		prepareConfig();
	}
	
	private void prepareConfig() {
		System.out.println("Grounding: " +grounding.getTransportProtocol().getPort());
		if (grounding.getTransportProtocol() instanceof KafkaTransportProtocol)
			prepareKafkaConfig();
		else if (grounding.getTransportProtocol() instanceof JmsTransportProtocol)
			prepareJmsConfig();
		else
			prepareKafkaConfig();
	}

	private void prepareJmsConfig() {
		this.camelConfig = new CamelConfig.ActiveMQ(brokerAlias, grounding.getTransportProtocol().getUri() +":" +grounding.getTransportProtocol().getPort());
		this.endpointUri = brokerAlias + ":topic:";
	}

	private void prepareKafkaConfig() {
		KafkaTransportProtocol protocol = (KafkaTransportProtocol) grounding.getTransportProtocol();
		//this.camelConfig = new CamelConfig.Kafka(brokerAlias, grounding.getUri(), grounding.getPort());
		this.camelConfig = new CamelConfig.Kafka(brokerAlias, protocol.getUri(), protocol.getZookeeperPort());
		//this.endpointUri = brokerAlias + ":topic:";
		this.endpointUri = brokerAlias +":" +protocol.getUri() +":" +protocol.getPort() +"?zookeeperHost=" +protocol.getUri() +"&zookeeperPort=" +protocol.getZookeeperPort() +"&groupId=group1&topic=";
	}

	public CamelConfig getCamelConfig()
	{
		return camelConfig;
	}
	
	public String getEndpointUri(String topicName)
	{
		return endpointUri +topicName;
	}
	
	public String getBrokerAlias()
	{
		return brokerAlias;
	}
	
	public String getBrokerUrl()
	{
		return grounding.getTransportProtocol().getUri();
	}
	
	public String getBrokerUrlWithPort()
	{
		return grounding.getTransportProtocol().getUri() +":" +grounding.getTransportProtocol().getPort();
	}
}
