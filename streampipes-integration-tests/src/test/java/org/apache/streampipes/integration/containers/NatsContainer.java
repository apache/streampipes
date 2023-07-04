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
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

public class NatsContainer extends GenericContainer<NatsContainer> {

  protected static final int NATS_PORT = 4222;

  public NatsContainer() {
    super("nats:latest");
  }

  public void start() {
    this.withExposedPorts(NATS_PORT);
    this.waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server is ready.*"));
    super.start();
  }

  public String getBrokerHost() {
    return getHost();
  }

  public Integer getBrokerPort() {
    return getMappedPort(NATS_PORT);
  }

  public String getBrokerUrl() {
    return getBrokerHost() + ":" + getBrokerPort();
  }
}
