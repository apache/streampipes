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

package org.apache.streampipes.manager.matching.v2;

import org.apache.streampipes.container.declarer.EventStreamDeclarer;
import org.apache.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.apache.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.vocabulary.MessageFormat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {

	public static TransportProtocol kafkaProtocol() {
		return new KafkaTransportProtocol("localhost", 9092, "abc", "localhost", 2181);
	}

	public static TransportProtocol jmsProtocol() {
		return new JmsTransportProtocol("localhost", 61616, "abc");
	}

	public static TransportFormat jsonFormat() {
		return new TransportFormat(MessageFormat.JSON);
	}
	
	public static TransportFormat thriftFormat() {
		return new TransportFormat(MessageFormat.THRIFT);
	}
	
	public static Pipeline makePipeline(SemanticEventProducerDeclarer producer, EventStreamDeclarer stream, SemanticEventProcessingAgentDeclarer agent) {
		DataSourceDescription dataSourceDescription = new DataSourceDescription(producer.declareModel());
		dataSourceDescription.setElementId("http://www.schema.org/test1");
		SpDataStream offer = stream.declareModel();
		offer.setElementId("http://www.schema.org/test2");
		DataProcessorDescription requirement = (agent.declareModel());
		requirement.setElementId("http://www.schema.org/test3");
		Pipeline pipeline = new Pipeline();
		SpDataStream offeredClientModel = offer;
		offeredClientModel.setDom("A");

		DataProcessorInvocation requiredClientModel = new DataProcessorInvocation(requirement);
		requiredClientModel.setDom("B");
		requiredClientModel.setConnectedTo(Arrays.asList("A"));
		
		pipeline.setStreams(Arrays.asList(offeredClientModel));
		pipeline.setSepas(Arrays.asList(requiredClientModel));
		
		
		return pipeline;
	}

	public static Pipeline makePipeline(List<SpDataStream> streams, List<DataProcessorInvocation> epas) {
		Pipeline pipeline = new Pipeline();

		pipeline.setStreams(streams.stream().map(s -> new SpDataStream(s)).collect(Collectors.toList()));
		pipeline.setSepas(epas.stream().map(s -> new DataProcessorInvocation(s)).collect(Collectors.toList()));

		return pipeline;
	}

    public static DataProcessorInvocation makeSepa(SemanticEventProcessingAgentDeclarer declarer, String domId, String... connectedTo) {
        DataProcessorInvocation invocation = new DataProcessorInvocation(declarer.declareModel());
        invocation.setDom(domId);
        invocation.setConnectedTo(Arrays.asList(connectedTo));
        return invocation;
    }

    public static SpDataStream makeStream(SemanticEventProducerDeclarer declarer, EventStreamDeclarer streamDec, String domId) {
        SpDataStream stream = new SpDataStream(streamDec.declareModel());
        stream.setDom(domId);
        return stream;
    }
	
}
