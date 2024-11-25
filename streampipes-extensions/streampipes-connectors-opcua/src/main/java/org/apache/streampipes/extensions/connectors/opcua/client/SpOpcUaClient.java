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

package org.apache.streampipes.extensions.connectors.opcua.client;


import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.connectors.opcua.config.MiloOpcUaConfigurationProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/***
 * Wrapper class for all OPC UA specific stuff.
 */
public class SpOpcUaClient<T extends OpcUaConfig> {

  private static final Logger LOG = LoggerFactory.getLogger(SpOpcUaClient.class);

  private OpcUaClient client;
  private final T spOpcConfig;

  public SpOpcUaClient(T config) {
    this.spOpcConfig = config;
  }

  /***
   * Establishes appropriate connection to OPC UA endpoint depending on the {@link SpOpcUaClient} instance
   *
   * @throws UaException An exception occurring during OPC connection
   */
  public ConnectedOpcUaClient connect()
      throws UaException, ExecutionException, InterruptedException, SpConfigurationException, URISyntaxException {
    OpcUaClientConfig clientConfig = new MiloOpcUaConfigurationProvider().makeClientConfig(spOpcConfig);
    var client = OpcUaClient.create(clientConfig);
    client.connect().get();
    return new ConnectedOpcUaClient(client);
  }





  public T getSpOpcConfig() {
    return spOpcConfig;
  }
}
