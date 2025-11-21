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

public class MosquittoContainer extends GenericContainer<MosquittoContainer> {

  protected static final Integer[] MOSQUITTO_PORTS = {1883, 8883};

  public MosquittoContainer() {
    super("eclipse-mosquitto:latest");
  }

  public void start() {
    this.withExposedPorts(MOSQUITTO_PORTS);
    this.withClasspathResourceMapping(
        "mosquitto.conf",
        "/mosquitto/config/mosquitto.conf",
        BindMode.READ_ONLY);
    this.withClasspathResourceMapping(
        "mosquitto.crt",
        "/mosquitto/config/mosquitto.crt",
        BindMode.READ_ONLY);
      this.withClasspathResourceMapping(
        "mosquitto.key",
        "/mosquitto/config/mosquitto.key",
       BindMode.READ_ONLY);
        this.withClasspathResourceMapping(
        "passwd",
        "/mosquitto/config/passwd",
        BindMode.READ_ONLY);
    super.start();
  }

  public String getBrokerHost() {
    return getHost();
  }

  public Integer getBrokerPort() {
    return getMappedPort(MOSQUITTO_PORTS[0]);
  }
  public Integer getBrokerTLSPort() {
    return getMappedPort(MOSQUITTO_PORTS[1]);
  }

  public String getBrokerUrl() {
    return "tcp://" + getBrokerHost() + ":" + getBrokerPort();
  }

  public String getBrokerUrlTLS() {
    return "ssl://" + getBrokerHost() + ":" + getBrokerTLSPort();
  }
}
