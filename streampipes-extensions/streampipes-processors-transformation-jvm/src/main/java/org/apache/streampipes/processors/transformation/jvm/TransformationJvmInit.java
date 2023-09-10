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
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.processors.transformation.jvm.processor.array.count.CountArrayProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.array.split.SplitArrayProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.counter.BooleanCounterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.edge.SignalEdgeFilterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter.BooleanInverterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.BooleanOperatorProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state.BooleanToStateProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping.BooleanTimekeepingProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timer.BooleanTimerProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.fieldrename.FiledRenameProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.hasher.FieldHasherProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.mapper.FieldMapperProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.measurementconverter.MeasurementUnitConverterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.round.RoundProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number.NumberLabelerProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter.StringCounterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state.StringToStateProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.task.TaskDurationProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.timestampextractor.TimestampExtractorProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.transformtoboolean.TransformToBooleanProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.value.change.ChangedValueDetectionProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.value.duration.CalculateDurationProcessor;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.wrapper.standalone.runtime.StandaloneStreamPipesRuntimeProvider;

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
        .registerRuntimeProvider(new StandaloneStreamPipesRuntimeProvider())
        .registerPipelineElements(
            new CountArrayProcessor(),
            new SplitArrayProcessor(),
            new CalculateDurationProcessor(),
            new ChangedValueDetectionProcessor(),
            new TimestampExtractorProcessor(),
            new BooleanCounterProcessor(),
            new BooleanInverterProcessor(),
            new BooleanTimekeepingProcessor(),
            new BooleanTimerProcessor(),
            new CsvMetadataEnrichmentProcessor(),
            new FieldHasherProcessor(),
            new FieldMapperProcessor(),
            new MeasurementUnitConverterProcessor(),
            new TaskDurationProcessor(),
            new TransformToBooleanProcessor(),
            new StringTimerProcessor(),
            new SignalEdgeFilterProcessor(),
            new BooleanToStateProcessor(),
            new NumberLabelerProcessor(),
            new StringToStateProcessor(),
            new StringCounterProcessor(),
            new BooleanOperatorProcessor(),
            new FiledRenameProcessor(),
            new RoundProcessor())
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
        .build();
  }
}
