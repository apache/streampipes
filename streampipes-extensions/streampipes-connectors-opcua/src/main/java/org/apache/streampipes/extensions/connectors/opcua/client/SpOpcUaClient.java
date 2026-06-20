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
import org.eclipse.milo.opcua.sdk.client.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.dtd.LegacyDataTypeManagerInitializer;
import org.eclipse.milo.opcua.sdk.client.typetree.DataTypeManagerFactory;
import org.eclipse.milo.opcua.stack.core.UaException;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/***
 * Wrapper class for all OPC UA specific stuff.
 */
public class SpOpcUaClient<T extends OpcUaConfig> {

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
    var legacyInitializer = new LegacyDataTypeManagerInitializer(client);
    var defaultInitializer = new DataTypeManagerFactory.DefaultInitializer();
    client.setDynamicDataTypeManagerFactory(DataTypeManagerFactory.eager((namespaceTable, dataTypeTree, dataTypeManager) -> {
      // Register legacy BSD codecs first and let modern DataTypeDefinition codecs override when available.
      legacyInitializer.initialize(namespaceTable, dataTypeTree, dataTypeManager);
      defaultInitializer.initialize(namespaceTable, dataTypeTree, dataTypeManager);
    }));
    try {
      client.connect();
      return new ConnectedOpcUaClient(client);
    } catch (Exception e) {
      client.disconnect();
      throw e;
    }
  }
}
