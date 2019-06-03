/*
 * Copyright 2017 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sinks.databases.jvm;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.streampipes.sinks.databases.jvm.config.DatabasesJvmConfig;
import org.streampipes.sinks.databases.jvm.couchdb.CouchDbController;
import org.streampipes.sinks.databases.jvm.influxdb.InfluxDbController;
import org.streampipes.sinks.databases.jvm.opcua.UpcUaController;
import org.streampipes.sinks.databases.jvm.postgresql.PostgreSqlController;

public class DatabasesJvmInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton
            .getInstance()
            .add(new CouchDbController())
            .add(new InfluxDbController())
            .add(new UpcUaController())
            .add(new PostgreSqlController());

    DeclarersSingleton.getInstance().registerDataFormat(new JsonDataFormatFactory());
    DeclarersSingleton.getInstance().registerProtocol(new SpKafkaProtocolFactory());
    DeclarersSingleton.getInstance().registerProtocol(new SpJmsProtocolFactory());

    new DatabasesJvmInit().init(DatabasesJvmConfig.INSTANCE);
  }
}
