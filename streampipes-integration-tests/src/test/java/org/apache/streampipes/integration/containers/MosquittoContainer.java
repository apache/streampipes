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

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class MosquittoContainer extends GenericContainer<MosquittoContainer> {

  protected static final int MOSQUITTO_PORT = 1883;

  public MosquittoContainer() {
    super("eclipse-mosquitto:latest");
  }

  public void start() {
    this.waitStrategy = Wait.forLogMessage(".*listen socket on port 1883.*", 1);
    this.withExposedPorts(MOSQUITTO_PORT);
    this.withClasspathResourceMapping(
        "mosquitto.conf",
        "/mosquitto/config/mosquitto.conf",
        BindMode.READ_ONLY);
    super.start();
  }

  public String getBrokerHost() {
    return getHost();
  }

  public Integer getBrokerPort() {
    return getMappedPort(MOSQUITTO_PORT);
  }

  public String getBrokerUrl() {
    return "tcp://" + getBrokerHost() + ":" + getBrokerPort();
  }
}
