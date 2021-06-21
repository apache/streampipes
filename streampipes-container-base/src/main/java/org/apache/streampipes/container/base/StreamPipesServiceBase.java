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
package org.apache.streampipes.container.base;

import org.apache.streampipes.commons.networking.Networking;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceRegistrationRequest;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class StreamPipesServiceBase {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesServiceBase.class);

  protected static final String AUTO_GENERATED_SERVICE_ID = UUID.randomUUID().toString();

  protected void startStreamPipesService(Class<?> serviceClass,
                                         String serviceGroup,
                                         String serviceId,
                                         Integer defaultPort) throws UnknownHostException {
    registerService(serviceGroup, serviceId, defaultPort);
    runApplication(serviceClass, defaultPort);
  }

  private void runApplication(Class<?> serviceClass,
                              Integer defaultPort) {
    SpringApplication app = new SpringApplication(serviceClass);
    app.setDefaultProperties(Collections.singletonMap("server.port", getPort(defaultPort)));
    app.run();
  }

  private void registerService(String serviceGroup,
                               String serviceId,
                               Integer defaultPort) throws UnknownHostException {
    SpServiceRegistrationRequest req = SpServiceRegistrationRequest.from(
            serviceGroup,
            serviceId,
            getHostname(),
            getPort(defaultPort),
            getServiceTags(),
            getHealthCheckPath());

    SpServiceDiscovery
            .getServiceDiscovery()
            .registerService(req);
  }

  protected String getHostname() throws UnknownHostException {
    return Networking.getHostname();
  }

  protected Integer getPort(Integer defaultPort) {
    return Networking.getPort(defaultPort);
  }

  protected abstract List<SpServiceTag> getServiceTags();

  protected void deregisterService(String serviceId) {
    LOG.info("Deregistering service (id={})...", serviceId);
    SpServiceDiscovery.getServiceDiscovery().deregisterService(serviceId);
  }

  protected String getHealthCheckPath() {
    return "";
  }

}
