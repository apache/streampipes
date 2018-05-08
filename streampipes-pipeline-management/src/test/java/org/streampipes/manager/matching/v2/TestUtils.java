/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.matching.v2;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.vocabulary.MessageFormat;

import java.net.URI;
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
		return new TransportFormat(MessageFormat.Json);
	}
	
	public static TransportFormat thriftFormat() {
		return new TransportFormat(MessageFormat.Thrift);
	}
	
	public static Pipeline makePipeline(SemanticEventProducerDeclarer producer, EventStreamDeclarer stream, SemanticEventProcessingAgentDeclarer agent) {
		DataSourceDescription dataSourceDescription = new DataSourceDescription(producer.declareModel());
		dataSourceDescription.setRdfId(new SupportsRdfId.URIKey(URI.create("http://www.schema.org/test1")));
		SpDataStream offer = stream.declareModel(dataSourceDescription);
		offer.setRdfId(new SupportsRdfId.URIKey(URI.create("http://www.schema.org/test2")));
		DataProcessorDescription requirement = (agent.declareModel());
		requirement.setRdfId(new SupportsRdfId.URIKey(URI.create("http://www.schema.org/test3")));
		Pipeline pipeline = new Pipeline();
		SpDataStream offeredClientModel = offer;
		offeredClientModel.setDOM("A");

		DataProcessorInvocation requiredClientModel = new DataProcessorInvocation(requirement);
		requiredClientModel.setDOM("B");
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
        invocation.setDOM(domId);
        invocation.setConnectedTo(Arrays.asList(connectedTo));
        return invocation;
    }

    public static SpDataStream makeStream(SemanticEventProducerDeclarer declarer, EventStreamDeclarer streamDec, String domId) {
        SpDataStream stream = new SpDataStream(streamDec.declareModel(declarer.declareModel()));
        stream.setDOM(domId);
        return stream;
    }
	
}
