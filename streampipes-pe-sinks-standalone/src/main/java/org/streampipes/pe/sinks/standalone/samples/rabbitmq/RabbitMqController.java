package org.streampipes.pe.sinks.standalone.samples.rabbitmq;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

/**
 * Created by riemer on 05.04.2017.
 */
public class RabbitMqController extends ActionController {
  @Override
  public SecDescription declareModel() {
    return DataSinkBuilder.create("rabbitmq", "RabbitMQ Publisher", "Forwards events to a " +
            "RabbitMQ broker")
            .iconUrl(ActionConfig.getIconUrl("rabbitmq-icon"))
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .requiredTextParameter(Labels.from("topic", "RabbitMQ Topic", "Select a RabbitMQ " +
                    "topic"), false, true)
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public boolean isVisualizable() {
    return false;
  }

  @Override
  public String getHtml(SecInvocation graph) {
    return null;
  }

  @Override
  public Response invokeRuntime(SecInvocation sec) {

    DataSinkParameterExtractor extractor = DataSinkParameterExtractor.from(sec);

    String consumerTopic = extractor.inputTopic(0);

    String publisherTopic = extractor.singleValueParameter("topic",
            String.class);

    startKafkaConsumer(ClientConfiguration.INSTANCE.getKafkaUrl(), consumerTopic,
            new RabbitMqConsumer(publisherTopic));

    String pipelineId = sec.getCorrespondingPipeline();
    return new Response(pipelineId, true);
  }

  @Override
  public Response detachRuntime(String pipelineId) {
    stopKafkaConsumer();
    return new Response(pipelineId, true);
  }
}
