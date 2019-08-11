/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.container.master.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.streampipes.connect.config.ConnectContainerConfig;

import java.util.Collections;

@SpringBootApplication
public class AdapterMasterContainer {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMasterContainer.class);

  public static void main(String... args) {

    Integer masterPort = ConnectContainerConfig.INSTANCE.getConnectContainerMasterPort();

    LOG.info("Started StreamPipes Connect Resource in MASTER mode");

    SpringApplication app = new SpringApplication(AdapterMasterContainer.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", masterPort));
    app.run();
  }
}
