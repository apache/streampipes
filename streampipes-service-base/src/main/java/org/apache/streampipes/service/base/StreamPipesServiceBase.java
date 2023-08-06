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

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.SpringApplication;

import java.net.UnknownHostException;
import java.util.Collections;

public abstract class StreamPipesServiceBase {

  public static final String AUTO_GENERATED_SERVICE_ID = RandomStringUtils.randomAlphanumeric(6);

  public void startStreamPipesService(Class<?> serviceClass,
                                         BaseNetworkingConfig networkingConfig) throws UnknownHostException {
    runApplication(serviceClass, networkingConfig.getPort());
  }

  private void runApplication(Class<?> serviceClass,
                              Integer port) {
    SpringApplication app = new SpringApplication(serviceClass);
    app.setDefaultProperties(Collections.singletonMap("server.port", port));
    app.run();
  }

  protected String getHealthCheckPath() {
    return "/svchealth/" + AUTO_GENERATED_SERVICE_ID;
  }

}
