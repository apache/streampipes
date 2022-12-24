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
package org.apache.streampipes.wrapper.standalone;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.runtime.ExternalEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneExternalEventProcessingDeclarer;

import com.google.gson.JsonObject;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class StreamPipesExternalDataProcessor
    extends StandaloneExternalEventProcessingDeclarer<ProcessorParams>
    implements ExternalEventProcessor<ProcessorParams> {

  // endpoint of Python processor runs here
  private static final String HTTP_PROTOCOL = "http://";
  private static final String PYTHON_ENDPOINT = "localhost:5000";

  private String invocationId;
  private String appId;
  private String inputTopic;
  private String outputTopic;
  private String kafkaUrl;

  private static String post(String endpoint, String payload) {
    String responseString = null;

    try {
      responseString = Request.Post(HTTP_PROTOCOL + PYTHON_ENDPOINT + "/" + endpoint)
          .bodyString(payload, ContentType.APPLICATION_JSON)
          .connectTimeout(1000)
          .socketTimeout(100000)
          .execute().returnContent().asString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return responseString;
  }

  @Override
  public ConfiguredExternalEventProcessor<ProcessorParams> onInvocation(DataProcessorInvocation graph,
                                                                        ProcessingElementParameterExtractor extractor) {
    EventProcessorBindingParams params = new ProcessorParams(graph);
    invocationId = UUID.randomUUID().toString();
    appId = graph.getAppId();
    inputTopic = getInputTopic(params);
    outputTopic = getOutputTopic(params);
    kafkaUrl = getKafkaUrl(params);

    Supplier<ExternalEventProcessor<ProcessorParams>> supplier = () -> this;
    return new ConfiguredExternalEventProcessor<>(new ProcessorParams(graph), supplier);
  }

  protected JsonObject createMinimalInvocationGraph(Map<String, String> staticPropertyMap) {
    JsonObject json = new JsonObject();

    json.addProperty("invocation_id", invocationId);
    json.addProperty("processor_id", appId);
    json.addProperty("input_topics", inputTopic);
    json.addProperty("output_topics", outputTopic);
    json.addProperty("bootstrap_servers", kafkaUrl);

    JsonObject staticProperties = new JsonObject();
    staticPropertyMap.forEach(staticProperties::addProperty);
    json.add("static_properties", staticProperties);

    return json;
  }

  protected void invoke(JsonObject json) {
    post("invoke", json.toString());
  }

  protected void detach() {
    JsonObject json = new JsonObject();
    json.addProperty("invocation_id", invocationId);
    post("detach", json.toString());
  }

  private String getInputTopic(EventProcessorBindingParams parameters) {
    return parameters
        .getGraph()
        .getInputStreams()
        .get(0)
        .getEventGrounding()
        .getTransportProtocol()
        .getTopicDefinition()
        .getActualTopicName();
  }

  private String getOutputTopic(EventProcessorBindingParams parameters) {
    return parameters
        .getGraph()
        .getOutputStream()
        .getEventGrounding()
        .getTransportProtocol()
        .getTopicDefinition()
        .getActualTopicName();
  }

  private String getKafkaUrl(EventProcessorBindingParams parameters) {
    String brokerHostname = parameters
        .getGraph()
        .getOutputStream()
        .getEventGrounding()
        .getTransportProtocols()
        .get(0)
        .getBrokerHostname();

    Integer kafkaPort = ((KafkaTransportProtocol) parameters
        .getGraph()
        .getOutputStream()
        .getEventGrounding()
        .getTransportProtocols()
        .get(0))
        .getKafkaPort();

    return brokerHostname + ":" + kafkaPort.toString();
  }

}


