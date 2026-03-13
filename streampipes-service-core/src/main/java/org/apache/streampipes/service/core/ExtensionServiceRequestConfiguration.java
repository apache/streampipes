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
package org.apache.streampipes.service.core;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.HttpExtensionServiceRequestManager;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;
import org.apache.streampipes.service.core.extensions.CoreExtensionTransportMode;
import org.apache.streampipes.service.core.extensions.CoreNatsRequestReplyClient;
import org.apache.streampipes.service.core.extensions.NatsExtensionServiceRequestManager;
import org.apache.streampipes.service.core.extensions.TransportAwareExtensionServiceRequestManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ExtensionServiceRequestConfiguration {

  @Bean(destroyMethod = "close")
  public CoreNatsRequestReplyClient coreNatsRequestReplyClient() {
    var env = Environments.getEnvironment();
    return new CoreNatsRequestReplyClient(
        env.getNatsHost().getValueOrDefault(),
        env.getNatsPort().getValueOrDefault(),
        Duration.ofSeconds(2)
    );
  }

  @Bean
  public NatsExtensionServiceRequestManager natsExtensionServiceRequestManager(
      CoreNatsRequestReplyClient coreNatsRequestReplyClient
  ) {
    var env = Environments.getEnvironment();
    var topicPrefix = env.getExtensionRequestTopicPrefix()
        .getValueOrReturn(ExtensionServiceBrokerTopics.DEFAULT_REQUEST_TOPIC_PREFIX);

    return new NatsExtensionServiceRequestManager(
        coreNatsRequestReplyClient,
        topicPrefix
    );
  }

  @Bean
  public ExtensionServiceRequestManager extensionServiceRequestManager(
      NatsExtensionServiceRequestManager natsExtensionServiceRequestManager
  ) {
    var env = Environments.getEnvironment();

    var transportMode = CoreExtensionTransportMode.from(
        env.getCoreExtensionTransportMode().getValueOrDefault()
    );

    return new TransportAwareExtensionServiceRequestManager(
        new HttpExtensionServiceRequestManager(),
        natsExtensionServiceRequestManager,
        transportMode
    );
  }

  @Bean
  public WorkerRestClient workerRestClient(ExtensionServiceRequestManager extensionServiceRequestManager) {
    return new WorkerRestClient(extensionServiceRequestManager);
  }
}
