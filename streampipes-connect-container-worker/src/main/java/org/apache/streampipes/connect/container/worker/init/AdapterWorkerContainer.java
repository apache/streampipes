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

package org.apache.streampipes.connect.container.worker.init;

import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.model.SpServiceDefinition;
import org.apache.streampipes.service.extensions.base.StreamPipesExtensionsServiceBase;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@Import({AdapterWorkerContainerResourceConfig.class})
public abstract class AdapterWorkerContainer extends StreamPipesExtensionsServiceBase {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterWorkerContainer.class);

  @Deprecated
  public void init(String workerUrl, String masterUrl, Integer workerPort) {
    throw new RuntimeException("Use the new SpServiceDefinitionBuilder to initialize a StreamPipes Connect adapter");
  }

  @Override
  protected List<SpServiceTag> getExtensionsServiceTags() {
    return new ConnectWorkerTagProvider().extractServiceTags();
  }

  @Override
  public void afterServiceRegistered(SpServiceDefinition serviceDef) {
    new ConnectWorkerRegistrationService().registerWorker(serviceDef.getServiceGroup());
  }

  @Override
  public void onExit() {
    deregisterService(DeclarersSingleton.getInstance().getServiceId());
  }

  @Override
  protected String getHealthCheckPath() {
    return "/worker";
  }
}
