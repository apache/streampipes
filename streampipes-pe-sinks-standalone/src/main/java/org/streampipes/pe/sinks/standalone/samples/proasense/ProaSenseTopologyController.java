package org.streampipes.pe.sinks.standalone.samples.proasense;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.commons.Utils;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.vocabulary.MessageFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProaSenseTopologyController extends ActionController {

	private ProaSenseEventNotifier eventNotifier;
	
	@Override
	public SecDescription declareModel() {
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("storm", "Online Analytics", "Processes event by Online Analytics");
		desc.setCategory(Arrays.asList(EcType.FORWARD.name()));
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		desc.setStaticProperties(staticProperties);
		
		EventGrounding grounding = new EventGrounding();

		grounding.setTransportProtocol(new KafkaTransportProtocol(ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(), "", ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort()));
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		desc.setSupportedGrounding(grounding);
		
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SecInvocation sec) {
		//String consumerUrl = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getBrokerHostname() + ":" +((JmsTransportProtocol)sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol()).getPort();
		String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		this.eventNotifier = new ProaSenseEventNotifier(consumerTopic);
		System.out.println(consumerTopic);
		//consumer = new ActiveMQConsumer(consumerUrl, consumerTopic);
		startKafkaConsumer(ClientConfiguration.INSTANCE.getKafkaUrl(), consumerTopic,
				new ProaSenseTopologyPublisher(sec, eventNotifier));
		
		//consumer.setListener(new ProaSenseTopologyPublisher(sec));
		
        String pipelineId = sec.getCorrespondingPipeline();
        return new Response(pipelineId, true);
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		stopKafkaConsumer();
        return new Response(pipelineId, true);
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		return new ProaSenseTopologyViewer(null, eventNotifier).generateHtml();
	}
	
	public String getClonedHtml(SecInvocation graph)
	{
		return new ProaSenseTopologyViewer(null, eventNotifier).generateHtml();
	}
}
