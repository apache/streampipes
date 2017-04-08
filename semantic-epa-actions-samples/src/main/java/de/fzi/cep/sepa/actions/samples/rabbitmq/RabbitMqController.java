package de.fzi.cep.sepa.actions.samples.rabbitmq;

import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.sdk.builder.DataSinkBuilder;
import de.fzi.cep.sepa.sdk.extractor.DataSinkParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.Labels;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

/**
 * Created by riemer on 05.04.2017.
 */
public class RabbitMqController extends ActionController {
  @Override
  public SecDescription declareModel() {
    return DataSinkBuilder.create("rabbitmq", "RabbitMQ Publisher", "Forwards events to a " +
            "RabbitMQ broker")
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
