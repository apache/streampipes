package org.streampipes.wrapper.flink.status;

import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.monitoring.ElementStatusInfoSettings;

/**
 * Created by riemer on 30.01.2017.
 */
public class PipelineElementStatusSenderFactory {

  public static <I extends InvocableStreamPipesEntity> PipelineElementStatusSender getStatusSender(I graph) {

    SpKafkaProducer kafkaProducer = new SpKafkaProducer();
    // TODO refactor

    return new PipelineElementStatusSender(kafkaProducer,
            graph.getStatusInfoSettings().getErrorTopic(),
            graph.getStatusInfoSettings().getStatsTopic());
  }

  private static <I extends InvocableStreamPipesEntity> String buildKafkaUrl(I graph) {

    ElementStatusInfoSettings settings = graph.getStatusInfoSettings();
    return settings.getKafkaHost() +":" +settings.getKafkaPort();
  }
}
