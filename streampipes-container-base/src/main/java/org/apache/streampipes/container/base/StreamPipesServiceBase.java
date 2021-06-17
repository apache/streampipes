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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public abstract class StreamPipesServiceBase {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesServiceBase.class);

  protected void startStreamPipesService(Class<?> serviceClass,
                                         String serviceGroup,
                                         String serviceName) {
    try {
      registerService(serviceGroup, serviceName);
      runApplication(serviceClass);
    } catch (UnknownHostException e) {
      LOG.error("Could not auto-resolve host address - please manually provide the hostname using the SP_HOST environment variable");
    }
  }

  private void runApplication(Class<?> serviceClass) {
    SpringApplication app = new SpringApplication(serviceClass);
    app.setDefaultProperties(Collections.singletonMap("server.port", getPort()));
    app.run();
  }

  private void registerService(String serviceGroup,
                               String serviceName) throws UnknownHostException {
    SpServiceDiscovery
            .getServiceDiscovery()
            .registerService(serviceGroup,
                    serviceName,
                    getHostname(),
                    getPort(),
                    getServiceTags());
  }

  protected String getHostname() throws UnknownHostException {
    return Networking.getHostname();
  }

  protected Integer getPort() {
    return Networking.getPort(getDefaultPort());
  }

  protected abstract Integer getDefaultPort();

  protected abstract List<String> getServiceTags();


}
