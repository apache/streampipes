package org.streampipes.wrapper.flink.status;

import com.google.gson.Gson;
import org.streampipes.messaging.kafka.SpKafkaProducer;

import java.io.Serializable;

/**
 * Created by riemer on 30.01.2017.
 */
public class PipelineElementStatusSender implements Serializable {

  private SpKafkaProducer kafkaProducer;

  private String errorTopic;
  private String statsTopic;

  private Gson gson;

  public PipelineElementStatusSender(SpKafkaProducer kafkaProducer, String errorTopic,
                                     String statsTopic) {

    this.kafkaProducer = kafkaProducer;
    this.errorTopic = errorTopic;
    this.statsTopic = statsTopic;

    this.gson = new Gson();
  }

}
