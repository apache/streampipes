package org.streampipes.pe.sinks.standalone.samples.notification;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.commons.Utils;
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
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.vocabulary.MessageFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationController extends ActionController {

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

		FreeTextStaticProperty contentProp = new FreeTextStaticProperty("content", "Content", "Enter the notification text. You can use place holders like #fieldName# to add the value of a stream variable.");
		contentProp.setMultiLine(true);
		contentProp.setHtmlAllowed(true);
		contentProp.setPlaceholdersSupported(true);
		staticProperties.add(contentProp);

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
		String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();

		startKafkaConsumer(ActionConfig.INSTANCE.getKafkaUrl(), consumerTopic,
				new NotificationProducer(sec));
		
		return new Response(sec.getElementId(), true);
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		stopKafkaConsumer();
		return new Response(pipelineId, true);
	}

}
