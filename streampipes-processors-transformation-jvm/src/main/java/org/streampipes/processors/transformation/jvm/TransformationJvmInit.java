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

package org.streampipes.processors.transformation.jvm;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.streampipes.dataformat.fst.FstDataFormatFactory;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.streampipes.processors.transformation.jvm.config.TransformationJvmConfig;
import org.streampipes.processors.transformation.jvm.processor.array.count.CountArrayController;
import org.streampipes.processors.transformation.jvm.processor.array.split.SplitArrayController;
import org.streampipes.processors.transformation.jvm.processor.booloperator.counter.BooleanCounterController;
import org.streampipes.processors.transformation.jvm.processor.booloperator.inverter.BooleanInverterController;
import org.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping.BooleanTimekeepingController;
import org.streampipes.processors.transformation.jvm.processor.booloperator.timer.BooleanTimerController;
import org.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentController;
import org.streampipes.processors.transformation.jvm.processor.task.TaskDurationController;
import org.streampipes.processors.transformation.jvm.processor.timestampextractor.TimestampExtractorController;
import org.streampipes.processors.transformation.jvm.processor.value.change.ChangedValueDetectionController;
import org.streampipes.processors.transformation.jvm.processor.value.duration.CalculateDurationController;

public class TransformationJvmInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton
            .getInstance()
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
            .add(new TaskDurationController());

    DeclarersSingleton.getInstance().registerDataFormats(new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory());

    DeclarersSingleton.getInstance().registerProtocols(new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory());

    new TransformationJvmInit().init(TransformationJvmConfig.INSTANCE);
  }
}
