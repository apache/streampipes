/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.model.grounding;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.util.Cloner;

@JsonSubTypes({
				@JsonSubTypes.Type(JmsTransportProtocol.class),
				@JsonSubTypes.Type(KafkaTransportProtocol.class),
				@JsonSubTypes.Type(MqttTransportProtocol.class),
				@JsonSubTypes.Type(NatsTransportProtocol.class)
})
public abstract class TransportProtocol extends UnnamedStreamPipesEntity {
	
	private static final long serialVersionUID = 7625791395504335184L;

	private String brokerHostname;

	private TopicDefinition topicDefinition;
	
	public TransportProtocol() {
		super();
	}
	
	public TransportProtocol(String hostname, TopicDefinition topicDefinition)
	{
		super();
		this.brokerHostname = hostname;
		this.topicDefinition = topicDefinition;
	}

	public TransportProtocol(TransportProtocol other) {
		super(other);
		this.brokerHostname = other.getBrokerHostname();
		if (other.getTopicDefinition() != null) {
			this.topicDefinition = new Cloner().topicDefinition(other.getTopicDefinition());
		}
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

}
