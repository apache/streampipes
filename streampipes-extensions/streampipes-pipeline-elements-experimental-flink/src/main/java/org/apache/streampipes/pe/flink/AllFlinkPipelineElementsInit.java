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
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.pe.flink.config.ConfigKeys;
import org.apache.streampipes.pe.flink.processor.absence.AbsenceController;
import org.apache.streampipes.pe.flink.processor.aggregation.AggregationController;
import org.apache.streampipes.pe.flink.processor.and.AndController;
import org.apache.streampipes.pe.flink.processor.boilerplate.BoilerplateController;
import org.apache.streampipes.pe.flink.processor.converter.FieldConverterController;
import org.apache.streampipes.pe.flink.processor.count.CountController;
import org.apache.streampipes.pe.flink.processor.eventcount.EventCountController;
import org.apache.streampipes.pe.flink.processor.gridenricher.SpatialGridEnrichmentController;
import org.apache.streampipes.pe.flink.processor.measurementunitonverter.MeasurementUnitConverterController;
import org.apache.streampipes.pe.flink.processor.peak.PeakDetectionController;
import org.apache.streampipes.pe.flink.processor.rate.EventRateController;
import org.apache.streampipes.pe.flink.processor.rename.FieldRenamerController;
import org.apache.streampipes.pe.flink.processor.sequence.SequenceController;
import org.apache.streampipes.pe.flink.processor.stat.summary.StatisticsSummaryController;
import org.apache.streampipes.pe.flink.processor.stat.window.StatisticsSummaryControllerWindow;
import org.apache.streampipes.pe.flink.processor.timestamp.TimestampController;
import org.apache.streampipes.pe.flink.processor.urldereferencing.UrlDereferencingController;
import org.apache.streampipes.pe.flink.processor.wordcount.WordCountController;
import org.apache.streampipes.pe.flink.sink.elasticsearch.ElasticSearchController;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.wrapper.flink.FlinkRuntimeProvider;


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
        .registerPipelineElements(new AggregationController(),
            new CountController(),
            new EventRateController(),
            new EventCountController(),
            new TimestampController(),
            new UrlDereferencingController(),
            new SpatialGridEnrichmentController(),
            new PeakDetectionController(),
            new SequenceController(),
            new AbsenceController(),
            new AndController(),
            new StatisticsSummaryController(),
            new StatisticsSummaryControllerWindow(),
            new WordCountController(),
            new FieldConverterController(),
            new MeasurementUnitConverterController(),
            new FieldRenamerController(),
            new BoilerplateController(),
            new ElasticSearchController())
        .registerRuntimeProvider(new FlinkRuntimeProvider())
        .registerMessagingFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory())
        .registerMessagingProtocols(
            new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpNatsProtocolFactory())
        .addConfig(ConfigKeys.ELASTIC_HOST, "elasticsearch", "Elastic search host address")
        .addConfig(ConfigKeys.ELASTIC_PORT_REST, 9200, "Elasitc search rest port")
        .build();
  }
}
