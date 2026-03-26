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

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class OpcUaDemoServerContainer extends GenericContainer<OpcUaDemoServerContainer> {

  public static final int OPC_UA_PORT = 4840;

  public OpcUaDemoServerContainer() {
    super(DockerImageName.parse("digitalpetri/opc-ua-demo-server:latest"));
  }

  @Override
  public void start() {
    this.withExposedPorts(OPC_UA_PORT);
    this.waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));
    super.start();
  }

  public String getEndpointUrl() {
    return String.format("opc.tcp://%s:%d/milo", getHost(), getMappedPort(OPC_UA_PORT));
  }
}
