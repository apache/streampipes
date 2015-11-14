package de.fzi.cep.sepa.actions.samples.evaluation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;

public class EvaluationController extends ActionController {

	EvaluationFileWriter fileWriter;
	String elementId;
	
	@Override
	public SecDescription declareModel() {
		
		SecDescription sec = new SecDescription("evaluation", "Evaluation File Output", "File Output used for evaluation purposes", "");
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(Arrays.asList(URI.create(SO.Number)));
		eventProperties.add(e1);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementId()), "property-timestamp", "Timestamp property", ""));
		FreeTextStaticProperty maxNumberOfRows = new FreeTextStaticProperty("path", "Path", "");
		staticProperties.add(maxNumberOfRows);

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		
		EventGrounding grounding = new EventGrounding();

		grounding.setTransportProtocol(new KafkaTransportProtocol(ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(), "", ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort()));
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
		
		String path = SepaUtils.getStaticPropertyByInternalName(sec, "path", FreeTextStaticProperty.class).getValue();
		
		EvaluationParameters fileParameters = new EvaluationParameters(inputTopic, brokerUrl, path, timestampProperty);
		
		fileWriter = new EvaluationFileWriter(fileParameters);
		Thread writeThread = new Thread(fileWriter);
		writeThread.start();
		
		return null;
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		fileWriter.setRunning(false);
		return new Response(this.elementId, true);
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation invocation) {
		// TODO Auto-generated method stub
		return null;
	}

}
