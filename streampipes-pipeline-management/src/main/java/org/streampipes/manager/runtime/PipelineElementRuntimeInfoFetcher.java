/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.manager.runtime;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.jms.ActiveMQConsumer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum PipelineElementRuntimeInfoFetcher {

  INSTANCE;

  Logger logger = LoggerFactory.getLogger(JsonParser.class);

  private static Set<String> consumerInstances = new HashSet<>();
  private Map<String, SpDataFormatConverter> converterMap;

  private static final String CONSUMER_GROUP_ID = "streampipes-backend-listener-group-";
  private static final String KAFKA_REST_ACCEPT = "application/vnd.kafka.binary.v2+json";
  private static final String KAFKA_REST_CONTENT_TYPE = "application/vnd.kafka.v2+json";

  private static final String OFFSET_FIELD_NAME = "offset";
  private static final String VALUE_FIELD_NAME = "value";


  PipelineElementRuntimeInfoFetcher() {
    this.converterMap = new HashMap<>();
  }

  public String getCurrentData(SpDataStream spDataStream) throws SpRuntimeException {

    if (spDataStream.getEventGrounding().getTransportProtocol() instanceof KafkaTransportProtocol) {
      return getLatestEventFromKafka(spDataStream);
    } else {
      return getLatestEventFromJms(spDataStream);
    }

  }

  private String getLatestEventFromJms(SpDataStream spDataStream) throws SpRuntimeException {
    final String[] result = {null};
    final String topic = getOutputTopic(spDataStream);
    if (!converterMap.containsKey(topic)) {
      this.converterMap.put(topic,
              new SpDataFormatConverterGenerator(getTransportFormat(spDataStream)).makeConverter());
    }
    ActiveMQConsumer consumer = new ActiveMQConsumer();
    consumer.connect((JmsTransportProtocol) spDataStream.getEventGrounding().getTransportProtocol(), new InternalEventProcessor<byte[]>() {
      @Override
      public void onEvent(byte[] event) {
        try {
          result[0] = converterMap.get(topic).convert(event);
          consumer.disconnect();
        } catch (SpRuntimeException e) {
          e.printStackTrace();
        }
      }
    });

    while (result[0] == null) {
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return result[0];
  }

  private TransportFormat getTransportFormat(SpDataStream spDataStream) {
    return spDataStream.getEventGrounding().getTransportFormats().get(0);
  }

  private String getLatestEventFromKafka(SpDataStream spDataStream) throws SpRuntimeException {
    String kafkaRestUrl = getKafkaRestUrl();
    String kafkaTopic = getOutputTopic(spDataStream);

    return getLatestSubscription(kafkaRestUrl, kafkaTopic, spDataStream);
  }

  private String getLatestSubscription(String kafkaRestUrl, String kafkaTopic,
                                       SpDataStream spDataStream) throws SpRuntimeException {
    String kafkaRestRecordsUrl = getConsumerInstanceUrl(kafkaRestUrl,
            getConsumerInstanceId(kafkaTopic), kafkaTopic) + "/records";

    try {
      if (!consumerInstances.contains(getConsumerInstanceId(kafkaTopic)) ||
              !converterMap.containsKey(kafkaTopic)) {
        createSubscription(kafkaRestUrl, kafkaTopic);
        consumerInstances.add(getConsumerInstanceId(kafkaTopic));
        converterMap.put(kafkaTopic,
                new SpDataFormatConverterGenerator(getTransportFormat(spDataStream)).makeConverter());
      }
      Response response = Request.Get(kafkaRestRecordsUrl)
              .addHeader(HttpHeaders.ACCEPT, KAFKA_REST_ACCEPT)
              .execute();

      return extractPayload(response.returnContent().asString(), spDataStream);
    } catch (IOException | SpRuntimeException e) {
      if (!e.getMessage().equals("")) {
        logger.error("Could not get any sample data from Kafka", e);
      }
      consumerInstances.remove(getConsumerInstanceId(kafkaTopic));

      throw new SpRuntimeException(e.getMessage());
    }
  }

  private void createSubscription(String kafkaRestUrl, String kafkaTopic) throws IOException, SpRuntimeException {
    String consumerInstance = getConsumerInstanceId(kafkaTopic);

    Integer statusCode = createConsumer(kafkaRestUrl, consumerInstance, kafkaTopic);
    Integer subscriptionStatusCode = subscribeConsumer(kafkaRestUrl, consumerInstance, kafkaTopic);
    if (subscriptionStatusCode != 204) {
      throw new SpRuntimeException("Could not read message form Kafka-REST: " + kafkaRestUrl);
    }

  }

  private Integer subscribeConsumer(String kafkaRestUrl, String consumerInstance, String kafkaTopic) throws IOException {
    String subscribeConsumerUrl = getConsumerInstanceUrl(kafkaRestUrl, consumerInstance, kafkaTopic)
            + "/subscription";


    return Request.Post(subscribeConsumerUrl)
            .addHeader(HttpHeaders.CONTENT_TYPE, KAFKA_REST_CONTENT_TYPE)
            .body(new StringEntity(makeSubscribeConsumerBody(kafkaTopic), Charsets.UTF_8))
            .execute()
            .returnResponse()
            .getStatusLine()
            .getStatusCode();
  }

  private String getConsumerInstanceUrl(String kafkaRestUrl, String consumerInstance, String topic) {
    return kafkaRestUrl + "/"
            + "consumers/"
            + getConsumerGroupId(topic)
            + "/instances/"
            + consumerInstance;
  }

  private String getConsumerGroupId(String topic) {
    return CONSUMER_GROUP_ID + topic;
  }

  private String makeSubscribeConsumerBody(String kafkaTopic) {
    return "{\"topics\":[\"" + kafkaTopic + "\"]}";
  }

  private Integer createConsumer(String kafkaRestUrl, String consumerInstance, String topic) throws IOException {
    String createConsumerUrl = kafkaRestUrl + "/consumers/" + getConsumerGroupId(topic);
    return Request.Post(createConsumerUrl)
            .addHeader(HttpHeaders.CONTENT_TYPE, KAFKA_REST_CONTENT_TYPE)
            .body(new StringEntity(makeCreateConsumerBody(consumerInstance), Charsets.UTF_8))
            .execute()
            .returnResponse()
            .getStatusLine()
            .getStatusCode();
  }

  private String makeCreateConsumerBody(String consumerInstance) {
    return "{\"name\": \""
            + consumerInstance
            + "\", \"format\": \"binary\", \"auto.offset.reset\": \"latest\"}";
  }

  private String getConsumerInstanceId(String kafkaTopic) {
    return CONSUMER_GROUP_ID + "-" + kafkaTopic;
  }

  private String getOutputTopic(SpDataStream spDataStream) {
    return spDataStream
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  private String getKafkaRestUrl() {
    return BackendConfig.INSTANCE.getKafkaRestUrl();
  }

  private String extractPayload(String rawResponse, SpDataStream spDataStream) throws SpRuntimeException {
    Long lastOffset = 0L;
    JsonElement jsonElement = new JsonParser().parse(rawResponse);
    JsonObject lastItem;

    if (jsonElement.isJsonArray()) {
      JsonArray allElements = jsonElement.getAsJsonArray();
      if (allElements.size() > 0) {
        lastItem = allElements.get(0).getAsJsonObject();
        lastOffset = lastItem.get(OFFSET_FIELD_NAME).getAsLong();
        for (int i = 1; i < allElements.size(); i++) {
          JsonObject obj = allElements.get(i).getAsJsonObject();
          Long offset = obj.get(OFFSET_FIELD_NAME).getAsLong();
          if (offset > lastOffset) {
            lastItem = obj;
          }
        }
        byte[] content = Base64
                .getDecoder()
                .decode(lastItem.get(VALUE_FIELD_NAME).getAsString());
        return converterMap.get(getOutputTopic(spDataStream)).convert(content);
      }
    }
    return "{}";
  }
}
