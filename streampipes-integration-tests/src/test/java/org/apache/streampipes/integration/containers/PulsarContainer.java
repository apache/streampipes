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
package org.apache.streampipes.integration.containers;

import org.apache.http.HttpStatus;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class PulsarContainer extends GenericContainer<PulsarContainer> {
  private static final int BROKER_HTTP_PORT = 8080;
  private static final int BROKER_SERVICE_PORT = 6650;

  public PulsarContainer() {
    super("apachepulsar/pulsar-test-latest-version:latest");
  }

  public void start() {
    this.waitStrategy = new HttpWaitStrategy()
        .forPort(BROKER_HTTP_PORT)
        .forStatusCode(HttpStatus.SC_OK)
        .forPath("/admin/v2/namespaces/public/default")
        .withStartupTimeout(Duration.of(300, SECONDS));
    this.withExposedPorts(BROKER_SERVICE_PORT, BROKER_HTTP_PORT);
    this.withCreateContainerCmdModifier(createContainerCmd -> {
      createContainerCmd.withEntrypoint("bin/pulsar");
    });
    this.setCommand("standalone -nfw -nss");
    super.start();
  }

  public String getBrokerHost() {
    return getHost();
  }

  public Integer getBrokerPort() {
    return getMappedPort(BROKER_SERVICE_PORT);
  }

  public String getHttpUrl() {
    return "http://" + getHost() + ":" + getMappedPort(BROKER_HTTP_PORT);
  }
}
