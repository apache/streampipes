package org.streampipes.pe.sinks.standalone.samples.wiki;

import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

/**
 * Created by riemer on 05.04.2017.
 */
public class WikiController extends ActionController {
  @Override
  public SecDescription declareModel() {
    return DataSinkBuilder.create("wikisink", "Wiki Sink", "Store the optimal route in the wiki")
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }


  @Override
  public Response invokeRuntime(SecInvocation sec) {

    DataSinkParameterExtractor extractor = DataSinkParameterExtractor.from(sec);

    String consumerTopic = extractor.inputTopic(0);

    String topic = "org.streampipes.kt2017.wiki";

    String kafkaHost = "ipe-koi15.fzi.de";
    int kafkaPort = 9092;

    startKafkaConsumer(ActionConfig.INSTANCE.getKafkaUrl(), consumerTopic,
            new WikiPublisher(new StreamPipesKafkaProducer(kafkaHost + ":" +kafkaPort, topic)));

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
    return false;
  }

  @Override
  public String getHtml(SecInvocation graph) {
    return null;
  }

}
