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

package org.apache.streampipes.sinks.databases.jvm;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.connectors.opcua.sink.OpcUaSink;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.sinks.databases.jvm.couchdb.CouchDbSink;
import org.apache.streampipes.sinks.databases.jvm.ditto.DittoSink;
import org.apache.streampipes.sinks.databases.jvm.iotdb.IotDbSink;
import org.apache.streampipes.sinks.databases.jvm.postgresql.PostgreSqlSink;
import org.apache.streampipes.sinks.databases.jvm.redis.RedisSink;

public class DatabasesJvmInit extends ExtensionsModelSubmitter {

  public static void main(String[] args) {
    new DatabasesJvmInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.sinks.databases.jvm",
            "Sinks Databases JVM",
            "",
            8090)
        .registerPipelineElements(
            new CouchDbSink(),
            new OpcUaSink(),
            new PostgreSqlSink(),
            new IotDbSink(),
            new DittoSink(),
            new RedisSink())
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
