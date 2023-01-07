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
package org.apache.streampipes.pe.flink;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processor.geo.flink.GeoFlinkInit;
import org.apache.streampipes.processors.aggregation.flink.AggregationFlinkInit;
import org.apache.streampipes.processors.enricher.flink.EnricherFlinkInit;
import org.apache.streampipes.processors.pattern.detection.flink.PatternDetectionFlinkInit;
import org.apache.streampipes.processors.statistics.flink.StatisticsFlinkInit;
import org.apache.streampipes.processors.textmining.flink.TextMiningFlinkInit;
import org.apache.streampipes.processors.transformation.flink.TransformationFlinkInit;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.sinks.databases.flink.DatabasesFlinkInit;


public class AllFlinkPipelineElementsInit extends ExtensionsModelSubmitter {

  public static void main(String[] args) {
    new AllFlinkPipelineElementsInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org-apache-streampipes-pe-all-flink",
                    "StreamPipes Bundled Pipeline Elements for JVM Wrapper",
                    "",
                    8090)
            .merge(new AggregationFlinkInit().provideServiceDefinition())
            .merge(new EnricherFlinkInit().provideServiceDefinition())
            .merge(new GeoFlinkInit().provideServiceDefinition())
            .merge(new PatternDetectionFlinkInit().provideServiceDefinition())
            .merge(new StatisticsFlinkInit().provideServiceDefinition())
            .merge(new TextMiningFlinkInit().provideServiceDefinition())
            .merge(new TransformationFlinkInit().provideServiceDefinition())
            .merge(new DatabasesFlinkInit().provideServiceDefinition())
            .registerMessagingFormats(
                    new JsonDataFormatFactory(),
                    new CborDataFormatFactory(),
                    new SmileDataFormatFactory(),
                    new FstDataFormatFactory())
            .registerMessagingProtocols(
                    new SpKafkaProtocolFactory(),
                    new SpJmsProtocolFactory(),
                    new SpMqttProtocolFactory())
            .build();
  }
}
