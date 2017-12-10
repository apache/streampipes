package org.streampipes.pe.sinks.standalone.samples.jms;


import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OntologyProperties;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class JmsController extends StandaloneEventSinkDeclarer<JmsParameters> {

	private static final String JMS_BROKER_SETTINGS_KEY = "broker-settings";
	private static final String TOPIC_KEY = "topic";

	private static final String JMS_HOST_URI = "http://schema.org/jmsHost";
	private static final String JMS_PORT_URI = "http://schema.org/jmsPort";
	
	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("jms", "JMS Publisher", "Publishes events to a JMS topic")
						.iconUrl(ActionConfig.getIconUrl("jms_logo"))
						.requiredPropertyStream1(EpRequirements.anyProperty())
						.requiredTextParameter(Labels.from(TOPIC_KEY, "JMS Topic", "Select a JMS " +
										"topic"), false, false)
						.requiredOntologyConcept(Labels.from(JMS_BROKER_SETTINGS_KEY, "JMS Broker Settings", "Provide" +
														" settings of the JMS broker to connect with."),
										OntologyProperties.mandatory(JMS_HOST_URI),
										OntologyProperties.mandatory(JMS_PORT_URI))
						.supportedFormats(SupportedFormats.jsonFormat())
						.supportedProtocols(SupportedProtocols.kafka())
						.build();
	}

	@Override
	public ConfiguredEventSink<JmsParameters, EventSink<JmsParameters>> onInvocation(DataSinkInvocation graph) {
		DataSinkParameterExtractor extractor = DataSinkParameterExtractor.from(graph);

		String topic = extractor.singleValueParameter(TOPIC_KEY,
						String.class);

		String jmsHost = extractor.supportedOntologyPropertyValue(JMS_BROKER_SETTINGS_KEY, JMS_HOST_URI,
						String.class);
		Integer jmsPort = extractor.supportedOntologyPropertyValue(JMS_BROKER_SETTINGS_KEY, JMS_PORT_URI,
						Integer.class);

		JmsParameters params = new JmsParameters(graph, jmsHost, jmsPort, topic);

		return new ConfiguredEventSink<>(params, JmsPublisher::new);
	}
}
