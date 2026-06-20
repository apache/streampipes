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

package org.apache.streampipes.extensions.connectors.opcua.config;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaNamingStrategy;

import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.ADAPTER_TYPE;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.AVAILABLE_NODES;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULLING_INTERVAL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PULL_MODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpOpcUaConfigExtractorTest {

  @Test
  void shouldExtractSelectedNodesWithoutStaticPropertyLookup() {
    IStaticPropertyExtractor extractor = mock(IStaticPropertyExtractor.class);

    when(extractor.selectedAlternativeInternalId(ADAPTER_TYPE.name()))
        .thenReturn(PULL_MODE.name());
    when(extractor.selectedAlternativeInternalId(OPC_HOST_OR_URL.name()))
        .thenReturn(OPC_URL.name());
    when(extractor.selectedAlternativeInternalId(SharedUserConfiguration.USER_AUTHENTICATION))
        .thenReturn(SharedUserConfiguration.USER_AUTHENTICATION_ANONYMOUS);
    when(extractor.selectedTreeNodesInternalNames(AVAILABLE_NODES.name(), String.class))
        .thenReturn(List.of("ns=2;s=Demo.DataTypeTest.ExtensionObject"));
    when(extractor.selectedSingleValueInternalName(SharedUserConfiguration.SECURITY_MODE, String.class))
        .thenReturn(MessageSecurityMode.None.name());
    when(extractor.selectedSingleValue(SharedUserConfiguration.SECURITY_POLICY, String.class))
        .thenReturn(SecurityPolicy.None.name());
    when(extractor.singleValueParameter(OPC_SERVER_URL.name(), String.class))
        .thenReturn("opc.tcp://localhost:4840/milo");
    when(extractor.singleValueParameter(PULLING_INTERVAL.name(), Integer.class))
        .thenReturn(1000);
    when(extractor.selectedSingleValueInternalName(
        SharedUserConfiguration.INCOMPLETE_EVENT_HANDLING_KEY,
        String.class
    )).thenReturn(SharedUserConfiguration.INCOMPLETE_OPTION_SEND);
    when(extractor.selectedSingleValueInternalName(OpcUaLabels.NAMING_STRATEGY.name(), String.class))
        .thenReturn(OpcUaNamingStrategy.DISPLAY_NAME.name());

    var config = SpOpcUaConfigExtractor.extractAdapterConfig(extractor, mock(IStreamPipesClient.class));

    assertEquals(List.of("ns=2;s=Demo.DataTypeTest.ExtensionObject"), config.getSelectedNodeNames());
  }
}
