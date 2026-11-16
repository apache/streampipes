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

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreContextStartupListenerTest {

  @Configuration(proxyBeanMethods = false)
  static class EmptyConfiguration {
  }

  private SpringApplication application(PreContextStartup startup) {
    var application = new SpringApplication(EmptyConfiguration.class);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.addListeners(new PreContextStartupListener(startup));
    return application;
  }

  @Test
  void startupRunsOnceLoggingIsInitialized() {
    // Spring Boot suppresses all log output from the start of the run until logging is initialized
    var loggingEnabled = new AtomicBoolean(false);

    try (var ignored = application(() -> {
      loggingEnabled.set(LoggerFactory.getLogger(PreContextStartupListenerTest.class).isInfoEnabled());
      return new BaseNetworkingConfig("localhost", 8090);
    }).run()) {
      assertTrue(loggingEnabled.get());
    }
  }

  @Test
  void portFromStartupIsAvailableToTheContext() {
    try (var context = application(() -> new BaseNetworkingConfig("localhost", 8123)).run()) {
      assertEquals("8123", context.getEnvironment().getProperty("server.port"));
    }
  }

  @Test
  void configuredPortWinsOverPortFromStartup() {
    try (var context = application(() -> new BaseNetworkingConfig("localhost", 8123))
        .run("--server.port=9000")) {
      assertEquals("9000", context.getEnvironment().getProperty("server.port"));
    }
  }

  @Test
  void unresolvableHostFailsStartupWithHint() {
    var application = application(() -> {
      throw new UnknownHostException("unknown");
    });

    var exception = assertThrows(IllegalStateException.class, application::run);

    assertTrue(exception.getMessage().contains("SP_HOST"));
  }
}
