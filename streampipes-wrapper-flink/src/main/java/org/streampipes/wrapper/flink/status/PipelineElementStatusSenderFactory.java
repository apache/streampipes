package org.streampipes.wrapper.flink.status;

import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.ElementStatusInfoSettings;

/**
 * Created by riemer on 30.01.2017.
 */
public class PipelineElementStatusSenderFactory {

  public static <I extends InvocableSEPAElement> PipelineElementStatusSender getStatusSender(I graph) {

    StreamPipesKafkaProducer kafkaProducer = new StreamPipesKafkaProducer(buildKafkaUrl(graph));

    return new PipelineElementStatusSender(kafkaProducer,
            graph.getStatusInfoSettings().getErrorTopic(),
            graph.getStatusInfoSettings().getStatsTopic());
  }

  private static <I extends InvocableSEPAElement> String buildKafkaUrl(I graph) {

    ElementStatusInfoSettings settings = graph.getStatusInfoSettings();
    return settings.getKafkaHost() +":" +settings.getKafkaPort();
  }
}
