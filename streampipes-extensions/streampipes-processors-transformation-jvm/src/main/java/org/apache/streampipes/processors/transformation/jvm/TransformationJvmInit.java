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

package org.apache.streampipes.processors.transformation.jvm;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processors.transformation.jvm.processor.array.count.CountArrayController;
import org.apache.streampipes.processors.transformation.jvm.processor.array.split.SplitArrayController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.counter.BooleanCounterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.edge.SignalEdgeFilterController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter.BooleanInverterController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.BooleanOperatorProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state.BooleanToStateController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping.BooleanTimekeepingController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timer.BooleanTimerController;
import org.apache.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentController;
import org.apache.streampipes.processors.transformation.jvm.processor.fieldrename.FiledRenameProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.hasher.FieldHasherProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.mapper.FieldMapperProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.measurementconverter.MeasurementUnitConverterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number.NumberLabelerController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter.StringCounterController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state.StringToStateController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerController;
import org.apache.streampipes.processors.transformation.jvm.processor.task.TaskDurationController;
import org.apache.streampipes.processors.transformation.jvm.processor.timestampextractor.TimestampExtractorController;
import org.apache.streampipes.processors.transformation.jvm.processor.transformtoboolean.TransformToBooleanController;
import org.apache.streampipes.processors.transformation.jvm.processor.value.change.ChangedValueDetectionController;
import org.apache.streampipes.processors.transformation.jvm.processor.value.duration.CalculateDurationController;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;

public class TransformationJvmInit extends ExtensionsModelSubmitter {

  public static void main(String[] args) {
    new TransformationJvmInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.processors.transformation.jvm",
            "Processors Transformation JVM",
            "",
            8090)
        .registerPipelineElements(
            new CountArrayController(),
            new SplitArrayController(),
            new CalculateDurationController(),
            new ChangedValueDetectionController(),
            new TimestampExtractorController(),
            new BooleanCounterProcessor(),
            new BooleanInverterController(),
            new BooleanTimekeepingController(),
            new BooleanTimerController(),
            new CsvMetadataEnrichmentController(),
            new FieldHasherProcessor(),
            new FieldMapperProcessor(),
            new MeasurementUnitConverterProcessor(),
            new TaskDurationController(),
            new TransformToBooleanController(),
            new StringTimerController(),
            new SignalEdgeFilterController(),
            new BooleanToStateController(),
            new NumberLabelerController(),
            new StringToStateController(),
            new StringCounterController(),
            new BooleanOperatorProcessor(),
            new FiledRenameProcessor())
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
