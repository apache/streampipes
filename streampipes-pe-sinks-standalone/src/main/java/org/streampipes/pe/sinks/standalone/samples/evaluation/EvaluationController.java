package org.streampipes.pe.sinks.standalone.samples.evaluation;

import org.streampipes.commons.Utils;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvaluationController extends ActionController {

	EvaluationFileWriter fileWriter;
	String elementId;
	
	@Override
	public SecDescription declareModel() {
		
		SecDescription sec = new SecDescription("evaluation", "Evaluation File Output", "File Output used for evaluation purposes", "");
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(Arrays.asList(URI.create(SO.Number), URI.create("http://schema.org/DateTime")));
		eventProperties.add(e1);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementId()), "property-timestamp", "Timestamp property", ""));
		
		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		
		EventGrounding grounding = new EventGrounding();

		grounding.setTransportProtocol(new KafkaTransportProtocol(ActionConfig.INSTANCE.getKafkaHost(),
				ActionConfig.INSTANCE.getKafkaPort(), "", ActionConfig.INSTANCE.getZookeeperHost(),
				ActionConfig.INSTANCE.getZookeeperPort()));
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		sec.setSupportedGrounding(grounding);
		
		return sec;
	}

	@Override
	public Response invokeRuntime(SecInvocation sec) {
		
		this.elementId = sec.getElementId();
		String brokerUrl = createKafkaUri(sec);
		String inputTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		
		String timestampProperty = SepaUtils.getMappingPropertyName(sec, "property-timestamp");
		
		EvaluationParameters fileParameters = new EvaluationParameters(inputTopic, brokerUrl, timestampProperty);
		
		fileWriter = new EvaluationFileWriter(fileParameters);
		Thread writeThread = new Thread(fileWriter);
		writeThread.start();
		String pipelineId = sec.getCorrespondingPipeline();
        return new Response(pipelineId, true);
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		fileWriter.setRunning(false);
		return new Response(this.elementId, true);
	}


}
