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

package org.apache.streampipes.integration.adapters.opcua;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaAdapter;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaNamingStrategy;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ADAPTER_TYPE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.AVAILABLE_NODES;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULL_MODE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpcUaAdapterTestHarness {

  public Map<String, Object> readSingleEvent(String endpointUrl, List<String> selectedNodeIds) throws Exception {
    var collectorQueue = new LinkedBlockingQueue<Map<String, Object>>();
    var extractor = makeExtractor(endpointUrl, selectedNodeIds);
    var runtimeContext = makeRuntimeContext();
    var adapter = new OpcUaAdapter(new OpcUaClientProvider());
    var started = false;

    try {
      adapter.onAdapterStarted(extractor, event -> collectorQueue.offer(new HashMap<>(event)), runtimeContext);
      started = true;

      Map<String, Object> event = collectorQueue.poll(20, TimeUnit.SECONDS);
      assertNotNull(event, "No event received from OPC UA adapter");
      return event;
    } finally {
      if (started) {
        adapter.onAdapterStopped(extractor, runtimeContext);
      }
    }
  }

  private IAdapterParameterExtractor makeExtractor(String endpointUrl, List<String> selectedNodeIds) {
    IStaticPropertyExtractor staticExtractor = mock(IStaticPropertyExtractor.class);

    when(staticExtractor.selectedAlternativeInternalId(ADAPTER_TYPE.name()))
        .thenReturn(PULL_MODE.name());
    when(staticExtractor.selectedAlternativeInternalId(OPC_HOST_OR_URL.name()))
        .thenReturn(OPC_URL.name());
    when(staticExtractor.selectedAlternativeInternalId(SharedUserConfiguration.USER_AUTHENTICATION))
        .thenReturn(SharedUserConfiguration.USER_AUTHENTICATION_ANONYMOUS);
    when(staticExtractor.selectedTreeNodesInternalNames(AVAILABLE_NODES.name(), String.class))
        .thenReturn(selectedNodeIds);
    when(staticExtractor.selectedSingleValueInternalName(SharedUserConfiguration.SECURITY_MODE, String.class))
        .thenReturn(MessageSecurityMode.None.name());
    when(staticExtractor.selectedSingleValue(SharedUserConfiguration.SECURITY_POLICY, String.class))
        .thenReturn(SecurityPolicy.None.name());
    when(staticExtractor.singleValueParameter(OPC_SERVER_URL.name(), String.class))
        .thenReturn(endpointUrl);
    when(staticExtractor.singleValueParameter(PULLING_INTERVAL.name(), Integer.class))
        .thenReturn(1000);
    when(staticExtractor.selectedSingleValueInternalName(
        SharedUserConfiguration.INCOMPLETE_EVENT_HANDLING_KEY,
        String.class
    )).thenReturn(SharedUserConfiguration.INCOMPLETE_OPTION_SEND);
    when(staticExtractor.selectedSingleValueInternalName(OpcUaLabels.NAMING_STRATEGY.name(), String.class))
        .thenReturn(OpcUaNamingStrategy.PARSED_NODE_ID.name());

    AdapterDescription adapterDescription = new AdapterDescription();
    adapterDescription.setElementId("opcua-adapter-it");

    IAdapterParameterExtractor extractor = mock(IAdapterParameterExtractor.class);
    when(extractor.getStaticPropertyExtractor()).thenReturn(staticExtractor);
    when(extractor.getAdapterDescription()).thenReturn(adapterDescription);
    return extractor;
  }

  private IAdapterRuntimeContext makeRuntimeContext() {
    IStreamPipesClient streamPipesClient = mock(IStreamPipesClient.class);
    IAdapterRuntimeContext runtimeContext = mock(IAdapterRuntimeContext.class);
    when(runtimeContext.getStreamPipesClient()).thenReturn(streamPipesClient);
    return runtimeContext;
  }
}

