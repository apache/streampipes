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

package org.apache.streampipes.sinks.notifications.jvm;

import org.apache.streampipes.container.model.SpServiceDefinition;
import org.apache.streampipes.container.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.sinks.notifications.jvm.config.ConfigKeys;
import org.apache.streampipes.sinks.notifications.jvm.email.EmailController;
import org.apache.streampipes.sinks.notifications.jvm.onesignal.OneSignalController;
import org.apache.streampipes.sinks.notifications.jvm.slack.SlackNotificationController;
import org.apache.streampipes.sinks.notifications.jvm.telegram.TelegramController;

public class SinksNotificationsJvmInit extends StandaloneModelSubmitter {

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.sinks.notifications.jvm",
            "Sinks Notifications JVM",
            "",
            8090)
        .registerPipelineElements(
            new EmailController(),
            new OneSignalController(),
            new SlackNotificationController(),
            new TelegramController())
        .registerMessagingFormats(new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory())
        .registerMessagingProtocols(new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory(),
            new SpMqttProtocolFactory())
        .addConfig(ConfigKeys.WEBSOCKET_PROTOCOL, "ws", "")
        .build();
  }
}
