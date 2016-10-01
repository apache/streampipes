package de.fzi.cep.sepa.actions.samples.notification;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.*;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationController extends ActionController {

	@Override
	public boolean isVisualizable() {
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("notification", "Notification", "Displays a notification in the UI panel", "");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/notification_icon.png");
		sec.setCategory(Arrays.asList(EcType.NOTIFICATION.name()));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new FreeTextStaticProperty("title", "Title", ""));
		staticProperties.add(new FreeTextStaticProperty("content", "Content", ""));

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
		String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();

		startKafkaConsumer(ClientConfiguration.INSTANCE.getKafkaUrl(), consumerTopic,
				new NotificationProducer(sec));
		
		return new Response(sec.getElementId(), true);
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		stopKafkaConsumer();
		return new Response(pipelineId, true);
	}

}
