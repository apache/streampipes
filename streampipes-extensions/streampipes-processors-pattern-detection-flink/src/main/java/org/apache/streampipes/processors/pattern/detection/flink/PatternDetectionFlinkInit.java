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

package org.apache.streampipes.processors.pattern.detection.flink;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processors.pattern.detection.flink.config.ConfigKeys;
import org.apache.streampipes.processors.pattern.detection.flink.processor.absence.AbsenceController;
import org.apache.streampipes.processors.pattern.detection.flink.processor.and.AndController;
import org.apache.streampipes.processors.pattern.detection.flink.processor.peak.PeakDetectionController;
import org.apache.streampipes.processors.pattern.detection.flink.processor.sequence.SequenceController;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;

public class PatternDetectionFlinkInit extends ExtensionsModelSubmitter {

  public static final String SERVICE_GROUP = "org.apache.streampipes.processors.patterndetection.flink";

  public static void main(String[] args) {
    new PatternDetectionFlinkInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create(SERVICE_GROUP,
            "Processors Pattern Detection Flink",
            "",
            8090)
        .registerPipelineElements(new PeakDetectionController(),
            new SequenceController(),
            new AbsenceController(),
            new AndController())
        .registerMessagingFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory())
        .registerMessagingProtocols(
            new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory(),
            new SpMqttProtocolFactory())
        .addConfig(ConfigKeys.FLINK_HOST, "jobmanager", "Hostname of the Flink Jobmanager")
        .addConfig(ConfigKeys.FLINK_PORT, 8081, "Port of the Flink Jobmanager")
        .addConfig(ConfigKeys.DEBUG, false, "Debug/Mini cluster mode of Flink program")
        .addConfig(ConfigKeys.FLINK_JAR_FILE_LOC, "./streampipes-processing-element-container.jar", "Jar file location")
        .build();
  }

}
