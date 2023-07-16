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
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package};

import ${package}.config.ConfigKeys;

import ${package}.pe.processor.${packageName}.${classNamePrefix}Controller;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.pulsar.SpPulsarProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;

public class Init extends ExtensionsModelSubmitter {

  public static void main(String[] args) throws Exception {
	  new Init().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("${package}",
              "Apache Flink processor",
              "",
              8090)
	  .registerPipelineElement(new ${classNamePrefix}Controller())
      .registerMessagingFormats(
              new JsonDataFormatFactory(),
              new CborDataFormatFactory(),
              new SmileDataFormatFactory(),
              new FstDataFormatFactory())
      .registerMessagingProtocols(
              new SpKafkaProtocolFactory(),
              new SpJmsProtocolFactory(),
              new SpMqttProtocolFactory(),
              new SpNatsProtocolFactory()),
              new SpPulsarProtocolFactory())
      .addConfig(ConfigKeys.FLINK_HOST, "jobmanager", "Hostname of the Flink Jobmanager")
      .addConfig(ConfigKeys.FLINK_PORT, 8081, "Port of the Flink Jobmanager")
      .addConfig(ConfigKeys.DEBUG, false, "Debug/Mini cluster mode of Flink program")
      .addConfig(ConfigKeys.FLINK_JAR_FILE_LOC, "./streampipes-processing-element-container.jar", "Jar file location")
      .addConfig(ConfigKeys.SERVICE_NAME, "sp fft stream analytics metrics", "Data processor service name")
      .addConfig(ConfigKeys.HOST, "${artifactId}", "Data processor host")
      .build();
  }
}
