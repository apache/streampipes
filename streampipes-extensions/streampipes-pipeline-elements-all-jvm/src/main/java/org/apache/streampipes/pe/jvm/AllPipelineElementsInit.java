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
package org.apache.streampipes.pe.jvm;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.connectors.influx.InfluxConnectorsInit;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processors.changedetection.jvm.ChangeDetectionJvmInit;
import org.apache.streampipes.processors.enricher.jvm.EnricherJvmInit;
import org.apache.streampipes.processors.filters.jvm.FiltersJvmInit;
import org.apache.streampipes.processors.geo.jvm.GeoJvmInit;
import org.apache.streampipes.processors.imageprocessing.jvm.ImageProcessingJvmInit;
import org.apache.streampipes.processors.siddhi.FiltersSiddhiInit;
import org.apache.streampipes.processors.textmining.jvm.TextMiningJvmInit;
import org.apache.streampipes.processors.transformation.jvm.TransformationJvmInit;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.sinks.brokers.jvm.BrokersJvmInit;
import org.apache.streampipes.sinks.databases.jvm.DatabasesJvmInit;
import org.apache.streampipes.sinks.internal.jvm.SinksInternalJvmInit;
import org.apache.streampipes.sinks.notifications.jvm.SinksNotificationsJvmInit;

public class AllPipelineElementsInit extends ExtensionsModelSubmitter {

  public static void main(String[] args) {
    new AllPipelineElementsInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org-apache-streampipes-pe-all-jvm",
            "StreamPipes Bundled Pipeline Elements for JVM Wrapper",
            "",
            8090)
        .merge(new SinksInternalJvmInit().provideServiceDefinition())
        .merge(new FiltersJvmInit().provideServiceDefinition())
        .merge(new ChangeDetectionJvmInit().provideServiceDefinition())
        .merge(new EnricherJvmInit().provideServiceDefinition())
        .merge(new FiltersSiddhiInit().provideServiceDefinition())
        .merge(new GeoJvmInit().provideServiceDefinition())
        .merge(new ImageProcessingJvmInit().provideServiceDefinition())
        .merge(new TextMiningJvmInit().provideServiceDefinition())
        .merge(new TransformationJvmInit().provideServiceDefinition())
        .merge(new BrokersJvmInit().provideServiceDefinition())
        .merge(new DatabasesJvmInit().provideServiceDefinition())
        .merge(new SinksNotificationsJvmInit().provideServiceDefinition())
        .merge(new InfluxConnectorsInit().provideServiceDefinition())
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
