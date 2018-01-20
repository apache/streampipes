package org.streampipes.model.grounding;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.TRANSPORT_PROTOCOL)
@Entity
@MappedSuperclass
public abstract class TransportProtocol extends UnnamedStreamPipesEntity {
	
	private static final long serialVersionUID = 7625791395504335184L;

	@RdfProperty(StreamPipes.BROKER_HOSTNAME)
	private String brokerHostname;

	@OneToOne(fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.TOPIC)
	private TopicDefinition topicDefinition;
	
	public TransportProtocol() {
		super();
	}
	
	public TransportProtocol(String uri, TopicDefinition topicDefinition)
	{
		super();
		this.brokerHostname = uri;
		this.topicDefinition = topicDefinition;
	}

	public TransportProtocol(TransportProtocol other) {
		super(other);
		this.brokerHostname = other.getBrokerHostname();
		this.topicDefinition = other.getTopicDefinition();
	}

	public String getBrokerHostname() {
		return brokerHostname;
	}

	public void setBrokerHostname(String uri) {
		this.brokerHostname = uri;
	}
	
	public TopicDefinition getTopicDefinition() {
		return topicDefinition;
	}

	public void setTopicDefinition(TopicDefinition topicDefinition) {
		this.topicDefinition = topicDefinition;
	}

	// TODO only kept for backwards compatibility, remove later
	@Deprecated
	public String getTopicName() {
		return topicDefinition.getActualTopicName();
	}

	@Deprecated
	public void setTopicName(String topicName) {
		if (this.topicDefinition == null) {
			this.topicDefinition = new SimpleTopicDefinition();
		}
		this.topicDefinition.setActualTopicName(topicName);
	}

}
