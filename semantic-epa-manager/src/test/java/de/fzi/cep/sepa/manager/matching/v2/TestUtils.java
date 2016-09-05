package de.fzi.cep.sepa.manager.matching.v2;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.clarkparsia.empire.SupportsRdfId;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

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
		SepDescription sepDescription = new SepDescription(producer.declareModel());
		sepDescription.setRdfId(new SupportsRdfId.URIKey(URI.create("http://www.schema.org/test1")));
		EventStream offer = stream.declareModel(sepDescription);
		offer.setRdfId(new SupportsRdfId.URIKey(URI.create("http://www.schema.org/test2")));
		SepaDescription requirement = (agent.declareModel());
		requirement.setRdfId(new SupportsRdfId.URIKey(URI.create("http://www.schema.org/test3")));
		Pipeline pipeline = new Pipeline();
		EventStream offeredClientModel = offer;
		offeredClientModel.setDOM("A");

		SepaInvocation requiredClientModel = new SepaInvocation(requirement);
		requiredClientModel.setDOM("B");
		requiredClientModel.setConnectedTo(Arrays.asList("A"));
		
		pipeline.setStreams(Arrays.asList(offeredClientModel));
		pipeline.setSepas(Arrays.asList(requiredClientModel));
		
		
		return pipeline;
	}

	public static Pipeline makePipeline(List<EventStream> streams, List<SepaInvocation> epas) {
		Pipeline pipeline = new Pipeline();

		pipeline.setStreams(streams.stream().map(s -> new EventStream(s)).collect(Collectors.toList()));
		pipeline.setSepas(epas.stream().map(s -> new SepaInvocation(s)).collect(Collectors.toList()));

		return pipeline;
	}

    public static SepaInvocation makeSepa(SemanticEventProcessingAgentDeclarer declarer, String domId, String... connectedTo) {
        SepaInvocation invocation = new SepaInvocation(declarer.declareModel());
        invocation.setDOM(domId);
        invocation.setConnectedTo(Arrays.asList(connectedTo));
        return invocation;
    }

    public static EventStream makeStream(SemanticEventProducerDeclarer declarer, EventStreamDeclarer streamDec, String domId) {
        EventStream stream = new EventStream(streamDec.declareModel(declarer.declareModel()));
        stream.setDOM(domId);
        return stream;
    }
	
}
