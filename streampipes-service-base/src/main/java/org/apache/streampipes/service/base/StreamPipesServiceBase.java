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
package org.apache.streampipes.service.base;

import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceRegistrationRequest;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public abstract class StreamPipesServiceBase {

  public static final String AUTO_GENERATED_SERVICE_ID = RandomStringUtils.randomAlphanumeric(6);
  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesServiceBase.class);

  protected void startStreamPipesService(Class<?> serviceClass,
                                         String serviceGroup,
                                         String serviceId,
                                         BaseNetworkingConfig networkingConfig) throws UnknownHostException {
    registerService(serviceGroup, serviceId, networkingConfig);
    runApplication(serviceClass, networkingConfig.getPort());
  }

  private void runApplication(Class<?> serviceClass,
                              Integer port) {
    SpringApplication app = new SpringApplication(serviceClass);
    app.setDefaultProperties(Collections.singletonMap("server.port", port));
    app.run();
  }

  private void registerService(String serviceGroup,
                               String serviceId,
                               BaseNetworkingConfig networkingConfig) {
    SpServiceRegistrationRequest req = SpServiceRegistrationRequest.from(
        serviceGroup,
        serviceId,
        networkingConfig.getHost(),
        networkingConfig.getPort(),
        getServiceTags(),
        getHealthCheckPath());

    SpServiceDiscovery
        .getServiceDiscovery()
        .registerService(req);
  }

  protected abstract List<SpServiceTag> getServiceTags();

  protected void deregisterService(String serviceId) {
    LOG.info("Deregistering service (id={})...", serviceId);
    SpServiceDiscovery.getServiceDiscovery().deregisterService(serviceId);
  }

  protected String getHealthCheckPath() {
    return "/svchealth/" + AUTO_GENERATED_SERVICE_ID;
  }

}
