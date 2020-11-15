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

import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.pe.flink.config.Config;
import org.apache.streampipes.processor.geo.flink.processor.gridenricher.SpatialGridEnrichmentController;
import org.apache.streampipes.processors.aggregation.flink.processor.aggregation.AggregationController;
import org.apache.streampipes.processors.aggregation.flink.processor.count.CountController;
import org.apache.streampipes.processors.aggregation.flink.processor.eventcount.EventCountController;
import org.apache.streampipes.processors.aggregation.flink.processor.rate.EventRateController;
import org.apache.streampipes.processors.enricher.flink.processor.math.mathop.MathOpController;
import org.apache.streampipes.processors.enricher.flink.processor.math.staticmathop.StaticMathOpController;
import org.apache.streampipes.processors.enricher.flink.processor.timestamp.TimestampController;
import org.apache.streampipes.processors.enricher.flink.processor.trigonometry.TrigonometryController;
import org.apache.streampipes.processors.enricher.flink.processor.urldereferencing.UrlDereferencingController;
import org.apache.streampipes.processors.pattern.detection.flink.processor.absence.AbsenceController;
import org.apache.streampipes.processors.pattern.detection.flink.processor.and.AndController;
import org.apache.streampipes.processors.pattern.detection.flink.processor.peak.PeakDetectionController;
import org.apache.streampipes.processors.pattern.detection.flink.processor.sequence.SequenceController;
import org.apache.streampipes.processors.statistics.flink.processor.stat.summary.StatisticsSummaryController;
import org.apache.streampipes.processors.statistics.flink.processor.stat.window.StatisticsSummaryControllerWindow;
import org.apache.streampipes.processors.textmining.flink.processor.wordcount.WordCountController;
import org.apache.streampipes.processors.transformation.flink.processor.boilerplate.BoilerplateController;
import org.apache.streampipes.processors.transformation.flink.processor.converter.FieldConverterController;
import org.apache.streampipes.processors.transformation.flink.processor.hasher.FieldHasherController;
import org.apache.streampipes.processors.transformation.flink.processor.mapper.FieldMapperController;
import org.apache.streampipes.processors.transformation.flink.processor.measurementUnitConverter.MeasurementUnitConverterController;
import org.apache.streampipes.processors.transformation.flink.processor.rename.FieldRenamerController;
import org.apache.streampipes.sinks.databases.flink.elasticsearch.ElasticSearchController;


public class AllFlinkPipelineElementsInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            // streampipes-processors-aggregation-flink
            .add(new AggregationController())
            .add(new CountController())
            .add(new EventRateController())
            .add(new EventCountController())
            // streampipes-processors-enricher-flink
            .add(new TimestampController())
            .add(new MathOpController())
            .add(new StaticMathOpController())
            .add(new UrlDereferencingController())
            .add(new TrigonometryController())
            // streampipes-processors-geo-flink
            .add(new SpatialGridEnrichmentController())
            // streampipes-processors-pattern-detection-flink
            .add(new PeakDetectionController())
            .add(new SequenceController())
            .add(new AbsenceController())
            .add(new AndController())
            // streampipes-processors-statistics-flink
            .add(new StatisticsSummaryController())
            .add(new StatisticsSummaryControllerWindow())
            // streampipes-processors-text-mining-flink
            .add(new WordCountController())
            // streampipes-processors-transformation-flink
            .add(new FieldConverterController())
            .add(new FieldHasherController())
            .add(new FieldMapperController())
            .add(new MeasurementUnitConverterController())
            .add(new FieldRenamerController())
            .add(new BoilerplateController())
            // streampipes-sinks-databases-flink
            .add(new ElasticSearchController());


    DeclarersSingleton.getInstance().registerDataFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory());

    DeclarersSingleton.getInstance().registerProtocols(
            new SpKafkaProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpJmsProtocolFactory());

    new AllFlinkPipelineElementsInit().init(Config.INSTANCE);
  }
}
