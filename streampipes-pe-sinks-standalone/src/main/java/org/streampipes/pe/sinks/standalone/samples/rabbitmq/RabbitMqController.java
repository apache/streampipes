package org.streampipes.pe.sinks.standalone.samples.rabbitmq;

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

public class RabbitMqController extends StandaloneEventSinkDeclarer<RabbitMqParameters> {

  private static final String RABBITMQ_BROKER_SETTINGS_KEY = "broker-settings";
  private static final String TOPIC_KEY = "topic";

  private static final String RABBITMQ_HOST_URI = "http://schema.org/rabbitMqHost";
  private static final String RABBITMQ_PORT_URI = "http://schema.org/rabbitMqPort";
  private static final String RABBITMQ_USER_URI = "http://schema.org/rabbitMqUser";
  private static final String RABBITMQ_PASSWORD_URI = "http://schema.org/rabbitMqPassword";
  private static final String EXCHANGE_NAME_URI = "http://schema.org/exchangeName";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("rabbitmq", "RabbitMQ Publisher", "Forwards events to a " +
            "RabbitMQ broker")
            .iconUrl(ActionConfig.getIconUrl("rabbitmq-icon"))
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .requiredTextParameter(Labels.from(TOPIC_KEY, "RabbitMQ Topic", "Select a RabbitMQ " +
                    "topic"), false, true)
            .requiredOntologyConcept(Labels.from(RABBITMQ_BROKER_SETTINGS_KEY, "RabbitMQ Broker Settings", "Provide" +
                    " settings of the RabbitMQ broker to connect with."),
                    OntologyProperties.mandatory(RABBITMQ_HOST_URI),
                    OntologyProperties.mandatory(RABBITMQ_PORT_URI),
                    OntologyProperties.mandatory(RABBITMQ_USER_URI),
                    OntologyProperties.mandatory(RABBITMQ_PASSWORD_URI),
                    OntologyProperties.optional(EXCHANGE_NAME_URI))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventSink<RabbitMqParameters, EventSink<RabbitMqParameters>> onInvocation(DataSinkInvocation graph) {
    DataSinkParameterExtractor extractor = DataSinkParameterExtractor.from(graph);
    String publisherTopic = extractor.singleValueParameter(TOPIC_KEY,
            String.class);

    String rabbitMqHost = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY, RABBITMQ_HOST_URI,
            String.class);
    Integer rabbitMqPort = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY, RABBITMQ_PORT_URI,
            Integer.class);
    String rabbitMqUser = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY, RABBITMQ_USER_URI,
            String.class);
    String rabbitMqPassword = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY,
            RABBITMQ_PASSWORD_URI,
            String.class);
    String exchangeName = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY,
            EXCHANGE_NAME_URI,
            String.class);

    RabbitMqParameters params = new RabbitMqParameters(graph, rabbitMqHost, rabbitMqPort, publisherTopic,
            rabbitMqUser, rabbitMqPassword, exchangeName);

    return new ConfiguredEventSink<>(params, RabbitMqConsumer::new);



  }
}
