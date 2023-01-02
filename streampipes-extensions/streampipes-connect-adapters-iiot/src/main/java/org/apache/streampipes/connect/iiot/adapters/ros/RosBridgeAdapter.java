/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.connect.iiot.adapters.ros;


import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.object.JsonObjectParser;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RosBridgeAdapter extends SpecificDataStreamAdapter implements ResolvesContainerProvidedOptions {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.ros";

  private static final String ROS_HOST_KEY = "ROS_HOST_KEY";
  private static final String ROS_PORT_KEY = "ROS_PORT_KEY";
  private static final String TOPIC_KEY = "TOPIC_KEY";

  private String topic;
  private String host;
  private int port;

  private Ros ros;

  private JsonObjectParser jsonObjectParser;

  public RosBridgeAdapter() {
  }

  public RosBridgeAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);

    getConfigurations(adapterDescription);

    this.jsonObjectParser = new JsonObjectParser();
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(AdapterType.Manufacturing)
        .requiredTextParameter(Labels.withId(ROS_HOST_KEY))
        .requiredTextParameter(Labels.withId(ROS_PORT_KEY))
        .requiredSingleValueSelectionFromContainer(Labels.withId(TOPIC_KEY), Arrays.asList(ROS_HOST_KEY, ROS_PORT_KEY))
        .build();

    return description;
  }

  @Override
  public void startAdapter() throws AdapterException {
    this.ros = new Ros(this.host, this.port);
    this.ros.connect();

    String topicType = getMethodType(this.ros, this.topic);

    ObjectMapper mapper = new ObjectMapper();
    Topic echoBack = new Topic(ros, this.topic, topicType);
    echoBack.subscribe(new TopicCallback() {
      @Override
      public void handleMessage(Message message) {


        try {
          Map<String, Object> result = mapper.readValue(message.toString(), HashMap.class);
          adapterPipeline.process(result);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    });


  }

  @Override
  public List<Option> resolveOptions(String requestId, StaticPropertyExtractor extractor) {
    String rosBridgeHost = extractor.singleValueParameter(ROS_HOST_KEY, String.class);
    Integer rosBridgePort = extractor.singleValueParameter(ROS_PORT_KEY, Integer.class);

    Ros ros = new Ros(rosBridgeHost, rosBridgePort);

    ros.connect();
    List<String> topics = getListOfAllTopics(ros);
    ros.disconnect();
    return topics.stream().map(Option::new).collect(Collectors.toList());
  }

  private class GetNEvents implements Runnable {

    private String topic;
    private String topicType;
    private Ros ros;

    private List<byte[]> events;

    public GetNEvents(String topic, String topicType, Ros ros) {
      this.topic = topic;
      this.topicType = topicType;
      this.ros = ros;
      this.events = new ArrayList<>();
    }

    @Override
    public void run() {
      Topic echoBack = new Topic(ros, this.topic, topicType);
      echoBack.subscribe(new TopicCallback() {
        @Override
        public void handleMessage(Message message) {
          events.add(message.toString().getBytes());
        }
      });
    }

    public List<byte[]> getEvents() {
      return this.events;
    }
  }

  @Override
  public void stopAdapter() throws AdapterException {
    this.ros.disconnect();
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {
    getConfigurations(adapterDescription);


    Ros ros = new Ros(host, port);

    boolean connect = ros.connect();

    if (!connect) {
      throw new AdapterException("Could not connect to ROS bridge Endpoint: " + host + " with port: " + port);
    }

    String topicType = getMethodType(ros, topic);

    GetNEvents getNEvents = new GetNEvents(topic, topicType, ros);
    Thread t = new Thread(getNEvents);
    t.start();

    while (getNEvents.getEvents().size() < 1) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    t.interrupt();

    ros.disconnect();

    EventSchema eventSchema = this.jsonObjectParser.getEventSchema(getNEvents.getEvents());

    GuessSchema guessSchema = new GuessSchema();

    guessSchema.setEventSchema(eventSchema);
    return guessSchema;
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new RosBridgeAdapter(adapterDescription);
  }

  @Override
  public String getId() {
    return ID;
  }

  private String getMethodType(Ros ros, String topic) {
    Service addTwoInts = new Service(ros, "/rosapi/topic_type", "rosapi/TopicType");
    ServiceRequest request = new ServiceRequest("{\"topic\": \"" + topic + "\"}");
    ServiceResponse response = addTwoInts.callServiceAndWait(request);

    JsonObject ob = new JsonParser().parse(response.toString()).getAsJsonObject();
    return ob.get("type").getAsString();
  }

  private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());
    this.host = extractor.singleValue(ROS_HOST_KEY, String.class);
    this.topic = extractor.selectedSingleValueOption(TOPIC_KEY);
    this.port = extractor.singleValue(ROS_PORT_KEY, Integer.class);
  }

  // Ignore for now, but is interesting for future implementations
  private List<String> getListOfAllTopics(Ros ros) {
    List<String> result = new ArrayList<>();
    Service service = new Service(ros, "/rosapi/topics", "rosapi/Topics");
    ServiceRequest request = new ServiceRequest();
    ServiceResponse response = service.callServiceAndWait(request);
    JsonObject ob = new JsonParser().parse(response.toString()).getAsJsonObject();

    if (ob.has("topics")) {
      JsonArray topics = ob.get("topics").getAsJsonArray();
      for (int i = 0; i < topics.size(); i++) {
        result.add(topics.get(i).getAsString());
      }

    }

    return result;

  }
}
