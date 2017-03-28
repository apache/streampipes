package de.fzi.cep.sepa.flink.status;

import com.google.gson.Gson;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.impl.StreamPipesRuntimeError;
import de.fzi.cep.sepa.model.impl.StreamPipesStatistics;

import java.io.Serializable;

/**
 * Created by riemer on 30.01.2017.
 */
public class PipelineElementStatusSender implements Serializable {

  private StreamPipesKafkaProducer kafkaProducer;

  private String errorTopic;
  private String statsTopic;

  private Gson gson;

  public PipelineElementStatusSender(StreamPipesKafkaProducer kafkaProducer, String errorTopic,
                                     String statsTopic) {

    this.kafkaProducer = kafkaProducer;
    this.errorTopic = errorTopic;
    this.statsTopic = statsTopic;

    this.gson = new Gson();
  }

  public void sendError(StreamPipesRuntimeError errorMessage) {
    kafkaProducer.publish(gson.toJson(errorMessage), errorTopic);
  }

  public void sendStatistics(StreamPipesStatistics statsMessage) {
    kafkaProducer.publish(gson.toJson(statsMessage), statsTopic);
  }
}
