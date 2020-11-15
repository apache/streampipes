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

import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processors.transformation.jvm.config.TransformationJvmConfig;
import org.apache.streampipes.processors.transformation.jvm.processor.array.count.CountArrayController;
import org.apache.streampipes.processors.transformation.jvm.processor.array.split.SplitArrayController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.counter.BooleanCounterController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.edge.SignalEdgeFilterController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter.BooleanInverterController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state.BooleanToStateController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping.BooleanTimekeepingController;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timer.BooleanTimerController;
import org.apache.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentController;
import org.apache.streampipes.processors.transformation.jvm.processor.state.buffer.StateBufferController;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.buffer.StateBufferLabelerController;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number.NumberLabelerController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter.StringCounterController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state.StringToStateController;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerController;
import org.apache.streampipes.processors.transformation.jvm.processor.task.TaskDurationController;
import org.apache.streampipes.processors.transformation.jvm.processor.timestampextractor.TimestampExtractorController;
import org.apache.streampipes.processors.transformation.jvm.processor.transformtoboolean.TransformToBooleanController;
import org.apache.streampipes.processors.transformation.jvm.processor.value.change.ChangedValueDetectionController;
import org.apache.streampipes.processors.transformation.jvm.processor.value.duration.CalculateDurationController;

public class TransformationJvmInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            .add(new CountArrayController())
            .add(new SplitArrayController())
            .add(new CalculateDurationController())
            .add(new ChangedValueDetectionController())
            .add(new TimestampExtractorController())
            .add(new BooleanCounterController())
            .add(new BooleanInverterController())
            .add(new BooleanTimekeepingController())
            .add(new BooleanTimerController())
            .add(new CsvMetadataEnrichmentController())
            .add(new TaskDurationController())
            .add(new BooleanInverterController())
            .add(new TransformToBooleanController())
            .add(new StringTimerController())
            .add(new SignalEdgeFilterController())
            .add(new BooleanToStateController())
            .add(new StateBufferController())
            .add(new StateBufferLabelerController())
            .add(new NumberLabelerController())
            .add(new StringToStateController())
            .add(new StringCounterController());

    DeclarersSingleton.getInstance().registerDataFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory());

    DeclarersSingleton.getInstance().registerProtocols(
            new SpKafkaProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpJmsProtocolFactory());

    new TransformationJvmInit().init(TransformationJvmConfig.INSTANCE);
  }
}
