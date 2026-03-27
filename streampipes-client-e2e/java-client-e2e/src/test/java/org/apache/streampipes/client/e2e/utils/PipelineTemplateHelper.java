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

package org.apache.streampipes.client.e2e.utils;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.output.KeepOutputStrategy;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.XSD;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builds adapter and pipeline models for E2E tests: Machine Simulator adapter, Boolean Filter processor,
 * and NATS sink, using the StreamPipes model API.
 */
final class PipelineTemplateHelper {

  private static final String MACHINE_SIMULATOR_APP_ID =
          "org.apache.streampipes.connect.iiot.adapters.simulator.machine";

  private static final String BOOLEAN_FILTER_APP_ID =
          "org.apache.streampipes.processors.filters.jvm.processor.booleanfilter";
  private static final String BOOLEAN_FILTER_BELONGS_TO = "sp:" + BOOLEAN_FILTER_APP_ID;

  private static final String NATS_SINK_APP_ID = "org.apache.streampipes.sinks.brokers.jvm.nats";
  private static final String NATS_SINK_BELONGS_TO = "sp:" + NATS_SINK_APP_ID;

  private static final String NATS_HOST = "nats";
  private static final int NATS_PORT = 4222;

  private static final URI XSD_INTEGER = XSD.INTEGER;
  private static final String XSD_STRING = XSD.STRING.toString();
  private static final String XSD_FLOAT = XSD.FLOAT.toString();
  private static final String XSD_BOOLEAN = XSD.BOOLEAN.toString();
  private static final String XSD_LONG = XSD.LONG.toString();

  private PipelineTemplateHelper() {
  }

  /**
   * Builds a Machine Simulator adapter description.
   */
  static AdapterDescription buildAdapter(String testPrefix, String topicIn) {
    AdapterDescription adapter = new AdapterDescription();
    adapter.setName(testPrefix + "adapter-" + UUID.randomUUID());
    adapter.setDescription("Java client e2e adapter");
    adapter.setAppId(MACHINE_SIMULATOR_APP_ID);
    adapter.setElementId("");
    adapter.setRev(null);
    adapter.setRunning(false);
    adapter.setSelectedEndpointUrl(null);
    adapter.setVersion(0);

    adapter.setEventGrounding(new EventGrounding(new NatsTransportProtocol(NATS_HOST, NATS_PORT, topicIn)));
    adapter.setConfig(buildAdapterConfig());
    adapter.setDataStream(buildMachineSimulatorDataStream(topicIn));
    return adapter;
  }

  /**
   * Builds a pipeline with Boolean Filter and NATS Sink.
   */
  static Pipeline buildPipeline(String testPrefix, AdapterDescription adapter, String topicIn, String topicOut) {
    SpDataStream stream = new Cloner().stream(adapter.getDataStream());
    stream.getEventGrounding().setTransportProtocol(new NatsTransportProtocol(NATS_HOST, NATS_PORT, topicIn));
    stream.setDom("s0");
    if (stream.getConnectedTo() == null) {
      stream.setConnectedTo(new ArrayList<>());
    }

    DataProcessorInvocation processor = buildBooleanFilterProcessor(stream, topicOut);
    processor.setDom("p0");
    processor.setConnectedTo(List.of("s0"));
    processor.setElementId("sp:processor:" + generateShortUuid());
    processor.getOutputStream().setDom("p0-out");

    DataSinkInvocation sink = buildNatsSink(processor.getOutputStream(), topicOut);
    sink.setDom("sink0");
    sink.setConnectedTo(List.of("p0"));
    sink.setElementId("sp:sink:" + generateShortUuid());

    Pipeline pipeline = new Pipeline();
    pipeline.setName(testPrefix + "pipeline-" + UUID.randomUUID());
    pipeline.setDescription("Java client e2e pipeline");
    pipeline.setPipelineId(UUID.randomUUID().toString().replace("-", ""));
    pipeline.setRev(null);
    pipeline.setStreams(List.of(stream));
    pipeline.setSepas(List.of(processor));
    pipeline.setActions(List.of(sink));
    return pipeline;
  }

  private static List<StaticProperty> buildAdapterConfig() {
    List<StaticProperty> config = new ArrayList<>();
    FreeTextStaticProperty waitTime = new FreeTextStaticProperty("wait-time-ms", "", "", XSD_INTEGER);
    waitTime.setValue("200");
    config.add(waitTime);
    FreeTextStaticProperty numSensors = new FreeTextStaticProperty("numberOfSensors", "", "", XSD_INTEGER);
    numSensors.setValue("1");
    config.add(numSensors);
    OneOfStaticProperty simulatorOption = new OneOfStaticProperty(
            "selected-simulator-option", "Simulator", "Select simulator type");
    simulatorOption.addOption(new Option("flowrate", true));
    simulatorOption.addOption(new Option("pressure", false));
    simulatorOption.addOption(new Option("waterlevel", false));
    simulatorOption.addOption(new Option("diagnostics", false));
    config.add(simulatorOption);
    return config;
  }

  private static SpDataStream buildMachineSimulatorDataStream(String topicIn) {
    SpDataStream stream = new SpDataStream();
    stream.setEventGrounding(new EventGrounding(new NatsTransportProtocol(NATS_HOST, NATS_PORT, topicIn)));
    stream.setEventSchema(buildFlowSimulatorEventSchema());
    return stream;
  }

  private static EventSchema buildFlowSimulatorEventSchema() {
    EventSchema schema = new EventSchema();
    schema.addEventProperty(primitive("eventId", XSD_STRING, "DIMENSION_PROPERTY"));
    schema.addEventProperty(primitive("timestamp", XSD_LONG, "HEADER_PROPERTY"));
    schema.addEventProperty(primitive("sensorId", XSD_STRING, "DIMENSION_PROPERTY"));
    schema.addEventProperty(primitive("mass_flow", XSD_FLOAT, "MEASUREMENT_PROPERTY"));
    schema.addEventProperty(primitive("volume_flow", XSD_FLOAT, "MEASUREMENT_PROPERTY"));
    schema.addEventProperty(primitive("temperature", XSD_FLOAT, "MEASUREMENT_PROPERTY"));
    schema.addEventProperty(primitive("density", XSD_FLOAT, "MEASUREMENT_PROPERTY"));
    schema.addEventProperty(primitive("sensor_fault_flags", XSD_BOOLEAN, "MEASUREMENT_PROPERTY"));
    return schema;
  }

  private static EventPropertyPrimitive primitive(String runtimeName, String runtimeType, String scope) {
    EventPropertyPrimitive p = new EventPropertyPrimitive(runtimeType, runtimeName, null, null);
    p.setPropertyScope(scope);
    return p;
  }

  private static DataProcessorInvocation buildBooleanFilterProcessor(SpDataStream inputStream, String outputTopic) {
    DataProcessorInvocation proc = new DataProcessorInvocation();
    proc.setAppId(BOOLEAN_FILTER_APP_ID);
    proc.setName("Boolean Filter");
    proc.setDescription("Retains events with a selected boolean value");
    proc.setBelongsTo(BOOLEAN_FILTER_BELONGS_TO);
    proc.setInputStreams(List.of(new Cloner().stream(inputStream)));
    proc.setStreamRequirements(List.of(new Cloner().stream(inputStream)));
    proc.setStaticProperties(buildBooleanFilterStaticProperties());
    proc.setSelectedEndpointUrl(null);
    proc.setOutputStrategies(List.of(new KeepOutputStrategy()));

    SpDataStream outputStream = new Cloner().stream(inputStream);
    outputStream.getEventGrounding().setTransportProtocol(new NatsTransportProtocol(NATS_HOST, NATS_PORT, outputTopic));
    proc.setOutputStream(outputStream);
    return proc;
  }

  private static List<StaticProperty> buildBooleanFilterStaticProperties() {
    List<StaticProperty> props = new ArrayList<>();
    MappingPropertyUnary mapping = new MappingPropertyUnary("boolean-mapping",
            "Boolean Field", "The field to filter on");
    mapping.setRequirementSelector("r0::boolean-mapping");
    mapping.setSelectedProperty("s0::sensor_fault_flags");
    mapping.setMapsFromOptions(List.of("s0::sensor_fault_flags"));
    mapping.setPropertyScope("NONE");
    props.add(mapping);

    OneOfStaticProperty valueProp = new OneOfStaticProperty("value", "Value", "Boolean value to pass through");
    valueProp.addOption(new Option("True", true));
    valueProp.addOption(new Option("False", false));
    props.add(valueProp);
    return props;
  }

  private static DataSinkInvocation buildNatsSink(SpDataStream inputStream, String outputTopic) {
    DataSinkInvocation sink = new DataSinkInvocation();
    sink.setName("NATS Sink");
    sink.setDescription("Publishes events to a NATS subject.");
    sink.setAppId(NATS_SINK_APP_ID);
    sink.setBelongsTo(NATS_SINK_BELONGS_TO);
    sink.setVersion(0);
    sink.setInputStreams(List.of(inputStream));
    sink.setStaticProperties(buildNatsSinkStaticProperties(outputTopic));
    sink.setSelectedEndpointUrl(null);
    return sink;
  }

  private static List<StaticProperty> buildNatsSinkStaticProperties(String outputTopic) {
    List<StaticProperty> props = new ArrayList<>();
    props.add(FreeTextStaticProperty.of("subject", outputTopic));
    props.add(FreeTextStaticProperty.of("natsUrls", "nats://nats:4222"));
    props.add(alternativesProperty("access-mode", "anonymous-alternative", "username-alternative"));
    props.add(alternativesProperty("connection-properties", "none-properties-alternative",
            "custom-properties-alternative"));
    return props;
  }

  private static StaticPropertyAlternatives alternativesProperty(String internalName,
                                                                 String selectedId,
                                                                 String unselectedId) {
    StaticPropertyAlternatives p = new StaticPropertyAlternatives(internalName, internalName, internalName);
    List<StaticPropertyAlternative> alts = new ArrayList<>();
    alts.add(alternative(selectedId, true));
    alts.add(alternative(unselectedId, false));
    p.setAlternatives(alts);
    return p;
  }

  private static StaticPropertyAlternative alternative(String internalName, boolean selected) {
    StaticPropertyAlternative a = new StaticPropertyAlternative(internalName, internalName, internalName);
    a.setSelected(selected);
    return a;
  }

  private static String generateShortUuid() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }
}
