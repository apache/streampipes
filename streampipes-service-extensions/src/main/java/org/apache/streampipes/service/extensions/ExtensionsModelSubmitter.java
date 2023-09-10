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
package org.apache.streampipes.service.extensions;

import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.service.extensions.connect.ConnectWorkerRegistrationService;
import org.apache.streampipes.service.extensions.function.StreamPipesFunctionHandler;
import org.apache.streampipes.service.extensions.security.WebSecurityConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import jakarta.annotation.PreDestroy;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@Import({ExtensionsResourceConfig.class, WebSecurityConfig.class})
public abstract class ExtensionsModelSubmitter extends StreamPipesExtensionsServiceBase {

  @PreDestroy
  public void onExit() {
    new ExtensionsServiceShutdownHandler().onShutdown();
    StreamPipesFunctionHandler.INSTANCE.cleanupFunctions();
    deregisterService(DeclarersSingleton.getInstance().getServiceId());
  }

  @Override
  public void afterServiceRegistered(SpServiceDefinition serviceDef) {
    new ConnectWorkerRegistrationService().registerWorker();
    StreamPipesFunctionHandler.INSTANCE.initializeFunctions(serviceDef.getServiceGroup());
  }

  @Override
  protected List<SpServiceTag> getExtensionsServiceTags() {
    return new ServiceTagProvider().extractServiceTags();
  }
}
