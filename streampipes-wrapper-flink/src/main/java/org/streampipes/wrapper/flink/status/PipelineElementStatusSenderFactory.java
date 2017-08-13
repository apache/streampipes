package org.streampipes.wrapper.flink.status;

import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.ElementStatusInfoSettings;

/**
 * Created by riemer on 30.01.2017.
 */
public class PipelineElementStatusSenderFactory {

  public static <I extends InvocableSEPAElement> PipelineElementStatusSender getStatusSender(I graph) {

    SpKafkaProducer kafkaProducer = new SpKafkaProducer();
    // TODO refactor

    return new PipelineElementStatusSender(kafkaProducer,
            graph.getStatusInfoSettings().getErrorTopic(),
            graph.getStatusInfoSettings().getStatsTopic());
  }

  private static <I extends InvocableSEPAElement> String buildKafkaUrl(I graph) {

    ElementStatusInfoSettings settings = graph.getStatusInfoSettings();
    return settings.getKafkaHost() +":" +settings.getKafkaPort();
  }
}
