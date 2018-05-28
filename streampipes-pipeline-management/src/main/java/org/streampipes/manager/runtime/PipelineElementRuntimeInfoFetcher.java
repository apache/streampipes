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
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.model.SpDataStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PipelineElementRuntimeInfoFetcher {

  private SpDataStream spDataStream;

  private static Set<String> consumerInstances = new HashSet<>();

  private static final String CONSUMER_GROUP_ID = "streampipes-backend-listener-group-";
  private static final String KAFKA_REST_CONTENT_TYPE = "application/vnd.kafka.v2+json";
  private static final String KAFKA_REST_SUBSCRIPTION_CONTENT_TYPE = "application/vnd.kafka.json.v2+json";

  private static final String OFFSET_FIELD_NAME = "offset";
  private static final String VALUE_FIELD_NAME = "value";

  public PipelineElementRuntimeInfoFetcher(SpDataStream spDataStream) {
    this.spDataStream = spDataStream;
  }

  public String getCurrentData() throws SpRuntimeException {
    String kafkaRestUrl = getKafkaRestUrl();
    String kafkaTopic = getOutputTopic();

    return getLatestSubscription(kafkaRestUrl, kafkaTopic);

  }

  private String getLatestSubscription(String kafkaRestUrl, String kafkaTopic) throws SpRuntimeException {
    String kafkaRestRecordsUrl = getConsumerInstanceUrl(kafkaRestUrl, getConsumerInstanceId(kafkaTopic), kafkaTopic) +"/records";

    try {
      if (!consumerInstances.contains(getConsumerInstanceId(kafkaTopic))) {
        createSubscription(kafkaRestUrl, kafkaTopic);
        consumerInstances.add(getConsumerInstanceId(kafkaTopic));
      }
      Response response = Request.Get(kafkaRestRecordsUrl)
              .addHeader(HttpHeaders.ACCEPT, KAFKA_REST_SUBSCRIPTION_CONTENT_TYPE)
              .execute();

      return extractPayload(response.returnContent().asString());
    } catch (IOException | SpRuntimeException e) {
      consumerInstances.remove(getConsumerInstanceId(kafkaTopic));
      throw new SpRuntimeException(e.getMessage());
    }
  }

  private void createSubscription(String kafkaRestUrl, String kafkaTopic) throws IOException, SpRuntimeException {
    String consumerInstance = getConsumerInstanceId(kafkaTopic);

    Integer statusCode = createConsumer(kafkaRestUrl, consumerInstance, kafkaTopic);
    Integer subscriptionStatusCode = subscribeConsumer(kafkaRestUrl, consumerInstance, kafkaTopic);
    if (subscriptionStatusCode != 204) {
      throw new SpRuntimeException("Could not read message");
    }

  }

  private Integer subscribeConsumer(String kafkaRestUrl, String consumerInstance, String kafkaTopic) throws IOException {
    String subscribeConsumerUrl = getConsumerInstanceUrl(kafkaRestUrl, consumerInstance, kafkaTopic)
            +"/subscription";


    return Request.Post(subscribeConsumerUrl)
            .addHeader(HttpHeaders.CONTENT_TYPE, KAFKA_REST_CONTENT_TYPE)
            .addHeader(HttpHeaders.ACCEPT, KAFKA_REST_CONTENT_TYPE)
            .body(new StringEntity(makeSubscribeConsumerBody(kafkaTopic), Charsets.UTF_8))
            .execute()
            .returnResponse()
            .getStatusLine()
            .getStatusCode();
  }

  private String getConsumerInstanceUrl(String kafkaRestUrl, String consumerInstance, String topic) {
    return kafkaRestUrl +"/"
            +"consumers/"
            +getConsumerGroupId(topic)
            +"/instances/"
            +consumerInstance;
  }

  private String getConsumerGroupId(String topic) {
    return CONSUMER_GROUP_ID + topic;
  }

  private String makeSubscribeConsumerBody(String kafkaTopic) {
    return "{\"topics\":[\"" +kafkaTopic +"\"]}";
  }

  private Integer createConsumer(String kafkaRestUrl, String consumerInstance, String topic) throws IOException {
    String createConsumerUrl = kafkaRestUrl +"/consumers/" +getConsumerGroupId(topic);
    return Request.Post(createConsumerUrl)
            .addHeader(HttpHeaders.CONTENT_TYPE, KAFKA_REST_CONTENT_TYPE)
            .addHeader(HttpHeaders.ACCEPT, KAFKA_REST_CONTENT_TYPE)
            .body(new StringEntity(makeCreateConsumerBody(consumerInstance), Charsets.UTF_8))
            .execute()
            .returnResponse()
            .getStatusLine()
            .getStatusCode();
  }

  private String makeCreateConsumerBody(String consumerInstance) {
    return "{\"name\": \""+consumerInstance +"\", \"format\": \"json\", \"auto.offset.reset\": \"latest\"}";
  }

  private String getConsumerInstanceId(String kafkaTopic) {
    return CONSUMER_GROUP_ID +"-" +kafkaTopic;
  }

  private String getOutputTopic() {
    return spDataStream
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  private String getKafkaRestUrl() {
    return BackendConfig.INSTANCE.getKafkaRestUrl();
  }

  private String extractPayload(String rawResponse) {
    Long lastOffset = 0L;
    JsonElement jsonElement = new JsonParser().parse(rawResponse);
    JsonObject lastItem;

    if (jsonElement.isJsonArray()) {
      JsonArray allElements = jsonElement.getAsJsonArray();
      if (allElements.size() > 0) {
        lastItem = allElements.get(0).getAsJsonObject();
        lastOffset = lastItem.get(OFFSET_FIELD_NAME).getAsLong();
        for(int i = 1; i < allElements.size(); i++) {
          JsonObject obj = allElements.get(i).getAsJsonObject();
          Long offset = obj.get(OFFSET_FIELD_NAME).getAsLong();
          if (offset > lastOffset) {
            lastItem = obj;
          }
        }
        return lastItem.get(VALUE_FIELD_NAME).getAsJsonObject().toString();
      }
    }
    return "{}";
  }
}
