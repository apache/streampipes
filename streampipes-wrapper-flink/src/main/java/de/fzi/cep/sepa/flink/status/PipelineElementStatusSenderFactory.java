package de.fzi.cep.sepa.flink.status;

import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.ElementStatusInfoSettings;

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
