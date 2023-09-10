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

package org.apache.streampipes.integration.svcdiscovery;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.variable.BooleanEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.IntEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;
import org.apache.streampipes.model.extensions.configuration.ConfigItem;

import org.mockito.Mockito;
import org.testcontainers.consul.ConsulContainer;
import org.testcontainers.utility.DockerImageName;

import static org.mockito.Mockito.mock;

public class AbstractConsulTest {

  static final ConsulContainer CONSUL_CONTAINER;

  static {
    CONSUL_CONTAINER = new ConsulContainer(
        DockerImageName.parse("consul").withTag("1.14.3"))
        .withExposedPorts(8500);
    CONSUL_CONTAINER.start();
  }

  protected ConfigItem makeConfigItem(String key, String value) {
    var item = new ConfigItem();
    item.setKey(key);
    item.setValue(value);
    item.setPassword(false);

    return item;
  }

  protected Environment mockEnvironment() {
    var envMock = mock(Environment.class);
    var hostVariableMock = mock(StringEnvironmentVariable.class);
    var portVariableMock = mock(IntEnvironmentVariable.class);
    var consulLocationMock = mock(StringEnvironmentVariable.class);
    var spDebugMock = mock(BooleanEnvironmentVariable.class);

    Mockito.when(hostVariableMock.getValueOrDefault()).thenReturn(CONSUL_CONTAINER.getHost());
    Mockito.when(portVariableMock.getValueOrDefault()).thenReturn(CONSUL_CONTAINER.getMappedPort(8500));
    Mockito.when(consulLocationMock.exists()).thenReturn(false);
    Mockito.when(spDebugMock.exists()).thenReturn(false);
    Mockito.when(spDebugMock.getValueOrReturn(false)).thenReturn(false);

    Mockito.when(envMock.getConsulHost()).thenReturn(hostVariableMock);
    Mockito.when(envMock.getConsulPort()).thenReturn(portVariableMock);
    Mockito.when(envMock.getConsulLocation()).thenReturn(consulLocationMock);
    Mockito.when(envMock.getSpDebug()).thenReturn(spDebugMock);

    return envMock;

  }
}
