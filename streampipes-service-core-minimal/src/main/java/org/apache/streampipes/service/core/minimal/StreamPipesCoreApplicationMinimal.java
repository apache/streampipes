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

package org.apache.streampipes.service.core.minimal;

import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.rest.security.SpPermissionEvaluator;
import org.apache.streampipes.service.core.OpenApiConfiguration;
import org.apache.streampipes.service.core.StreamPipesCoreApplication;
import org.apache.streampipes.service.core.StreamPipesPasswordEncoder;
import org.apache.streampipes.service.core.StreamPipesPrometheusConfig;
import org.apache.streampipes.service.core.WebSecurityConfig;
import org.apache.streampipes.service.core.WelcomePageController;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@Import({
    OpenApiConfiguration.class,
    SpPermissionEvaluator.class,
    StreamPipesPasswordEncoder.class,
    StreamPipesPrometheusConfig.class,
    WebSecurityConfig.class,
    WelcomePageController.class
})
@ComponentScan({
    "org.apache.streampipes.rest.*",
    "org.apache.streampipes.ps",
    "org.apache.streampipes.service.core.oauth2"})
public class StreamPipesCoreApplicationMinimal extends StreamPipesCoreApplication {

  public static void main(String[] args) {
    var application = new StreamPipesCoreApplicationMinimal();
    application.initialize(() -> List.of(
        new SpNatsProtocolFactory(),
        new SpMqttProtocolFactory()
    ));
  }
}
