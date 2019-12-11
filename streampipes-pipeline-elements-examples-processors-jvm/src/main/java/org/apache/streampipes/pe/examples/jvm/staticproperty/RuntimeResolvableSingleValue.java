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
package org.apache.streampipes.pe.examples.jvm.staticproperty;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.pe.examples.jvm.base.DummyEngine;
import org.apache.streampipes.pe.examples.jvm.base.DummyParameters;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.SupportedFormats;
import org.apache.streampipes.sdk.helpers.SupportedProtocols;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class RuntimeResolvableSingleValue extends
        StandaloneEventProcessingDeclarer<DummyParameters> implements ResolvesContainerProvidedOptions {

  private static final String KafkaHost = "kafka-host";
  private static final String KafkaPort = "kafka-port";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.examples.staticproperty" +
            ".runtimeresolvablesingle", "Runtime-resolvable single value example", "")
            .requiredStream(StreamRequirementsBuilder.
                    create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .outputStrategy(OutputStrategies.keep())
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .requiredTextParameter(Labels.from(KafkaHost, "Kafka Host", ""))
            .requiredIntegerParameter(Labels.from(KafkaPort, "Kafka Port", ""))

            // create a single value selection parameter that is resolved at runtime
            .requiredSingleValueSelectionFromContainer(Labels.from("id", "Example Name", "Example " +
                    "Description"), Arrays.asList(KafkaHost, KafkaPort))

            .build();
  }

  @Override
  public ConfiguredEventProcessor<DummyParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    // Extract the text parameter value
    String selectedSingleValue = extractor.selectedSingleValue("id", String.class);

    // now the text parameter would be added to a parameter class (omitted for this example)

    return new ConfiguredEventProcessor<>(new DummyParameters(graph), DummyEngine::new);
  }

  @Override
  public List<Option> resolveOptions(String requestId, StaticPropertyExtractor extractor) {
    String host = extractor.singleValueParameter(KafkaHost, String.class);
    Integer port = extractor.singleValueParameter(KafkaPort, Integer.class);

    String kafkaAddress = host + ":" + port;

    Properties props = new Properties();
    props.put("bootstrap.servers", kafkaAddress);
    props.put("group.id", "test-consumer-group");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

    KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
    Set<String> topics = consumer.listTopics().keySet();
    consumer.close();
    return topics.stream().map(Option::new).collect(Collectors.toList());
  }
}
