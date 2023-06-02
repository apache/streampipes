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


import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.management.connect.adapter.parser.ParserUtils;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
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
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RosBridgeAdapter implements StreamPipesAdapter, ResolvesContainerProvidedOptions {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.ros";

  private static final String ROS_HOST_KEY = "ROS_HOST_KEY";
  private static final String ROS_PORT_KEY = "ROS_PORT_KEY";
  private static final String TOPIC_KEY = "TOPIC_KEY";

  private String topic;
  private String host;
  private int port;


  private final ObjectMapper mapper = new ObjectMapper();

  private Ros ros;

  public RosBridgeAdapter() {
  }

  @Override
  public List<Option> resolveOptions(String requestId,
                                     IStaticPropertyExtractor extractor) {
    String rosBridgeHost = extractor.singleValueParameter(ROS_HOST_KEY, String.class);
    Integer rosBridgePort = extractor.singleValueParameter(ROS_PORT_KEY, Integer.class);

    Ros ros = new Ros(rosBridgeHost, rosBridgePort);

    ros.connect();
    List<String> topics = getListOfAllTopics(ros);
    ros.disconnect();
    return topics.stream().map(Option::new).collect(Collectors.toList());
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, RosBridgeAdapter::new)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Manufacturing)
        .requiredTextParameter(Labels.withId(ROS_HOST_KEY))
        .requiredTextParameter(Labels.withId(ROS_PORT_KEY))
        .requiredSingleValueSelectionFromContainer(
            Labels.withId(TOPIC_KEY), Arrays.asList(ROS_HOST_KEY, ROS_PORT_KEY))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    getConfigurations(extractor.getStaticPropertyExtractor());
    this.ros = new Ros(this.host, this.port);
    this.ros.connect();

    String topicType = getMethodType(this.ros, this.topic);

    Topic echoBack = new Topic(ros, this.topic, topicType);
    echoBack.subscribe(message -> {
      try {
        Map<String, Object> result = mapper.readValue(message.toString(), HashMap.class);
        collector.collect(result);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.ros.disconnect();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    getConfigurations(extractor.getStaticPropertyExtractor());


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
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    t.interrupt();

    ros.disconnect();

    try {
      Map<String, Object> event = mapper.readValue(getNEvents.getEvents().get(0), HashMap.class);
      return new ParserUtils().getGuessSchema(event);

    } catch (JsonProcessingException e) {
      throw new AdapterException("Could not guess event schema for %s".formatted(getNEvents.getEvents().get(0)), e);
    }
  }

  private class GetNEvents implements Runnable {

    private final String topic;
    private final String topicType;
    private final Ros ros;

    private final List<String> events;

    public GetNEvents(String topic, String topicType, Ros ros) {
      this.topic = topic;
      this.topicType = topicType;
      this.ros = ros;
      this.events = new ArrayList<>();
    }

    @Override
    public void run() {
      Topic echoBack = new Topic(ros, this.topic, topicType);
      echoBack.subscribe(message -> events.add(message.toString()));
    }

    public List<String> getEvents() {
      return this.events;
    }
  }

  private String getMethodType(Ros ros, String topic) {
    Service addTwoInts = new Service(ros, "/rosapi/topic_type", "rosapi/TopicType");
    ServiceRequest request = new ServiceRequest("{\"topic\": \"" + topic + "\"}");
    ServiceResponse response = addTwoInts.callServiceAndWait(request);

    JsonObject ob = new JsonParser().parse(response.toString()).getAsJsonObject();
    return ob.get("type").getAsString();
  }

  private void getConfigurations(IStaticPropertyExtractor extractor) {
    this.host = extractor.singleValueParameter(ROS_HOST_KEY, String.class);
    this.topic = extractor.selectedSingleValue(TOPIC_KEY, String.class);
    this.port = extractor.singleValueParameter(ROS_PORT_KEY, Integer.class);
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
