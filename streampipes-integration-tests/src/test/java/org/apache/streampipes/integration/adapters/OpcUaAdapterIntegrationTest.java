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

package org.apache.streampipes.integration.adapters;

import org.apache.streampipes.integration.adapters.opcua.OpcUaAdapterTestHarness;
import org.apache.streampipes.integration.adapters.opcua.OpcUaNodeDiscovery;
import org.apache.streampipes.integration.adapters.opcua.contract.OpcUaNodeContract;
import org.apache.streampipes.integration.adapters.opcua.contract.OpcUaNodeContracts;
import org.apache.streampipes.integration.containers.OpcUaDemoServerContainer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpcUaAdapterIntegrationTest {

  private final OpcUaAdapterTestHarness harness = new OpcUaAdapterTestHarness();

  @Test
  public void testOpcUaAdapterDataTypeTestStructures() throws Exception {
    try (var opcUaContainer = new OpcUaDemoServerContainer()) {
      opcUaContainer.start();

      String endpointUrl = opcUaContainer.getEndpointUrl();
      Map<String, String> dataTypeTestNodes = OpcUaNodeDiscovery.discoverDataTypeTestVariableNodes(endpointUrl);
      List<String> selectedNodeIds = OpcUaNodeDiscovery.selectStructureNodes(dataTypeTestNodes);

      assertTrue(selectedNodeIds.size() >= 3, "Expected at least 3 DataTypeTest variable nodes");

      Map<String, Object> event = harness.readSingleEvent(endpointUrl, selectedNodeIds);

      for (String nodeId : selectedNodeIds) {
        String expectedFieldName = OpcUaNodeContract.toParsedNodeIdFieldName(nodeId);
        assertTrue(
            event.containsKey(expectedFieldName),
            "Missing event field for node " + nodeId + " (expected key " + expectedFieldName + ")"
        );
      }

      long complexValues = event.values().stream().filter(value -> value instanceof Map<?, ?>).count();
      assertTrue(complexValues > 0, "Expected at least one complex structure value");
      assertTrue(containsNestedList(event), "Expected at least one matrix/array structure in event");
    }
  }

  @TestFactory
  public List<DynamicTest> testOpcUaAdapterNodeContracts() throws Exception {
    List<OpcUaNodeContract> contracts = OpcUaNodeContracts.all();

    try (var opcUaContainer = new OpcUaDemoServerContainer()) {
      opcUaContainer.start();
      String endpointUrl = opcUaContainer.getEndpointUrl();
      List<String> nodeIds = contracts.stream().map(OpcUaNodeContract::nodeId).toList();
      Map<String, Object> event = harness.readSingleEvent(endpointUrl, nodeIds);

      return contracts.stream()
          .map(contract -> DynamicTest.dynamicTest(contract.displayName(), () -> contract.assertAgainst(event)))
          .toList();
    }
  }

  private boolean containsNestedList(Object value) {
    if (value instanceof List<?> list) {
      if (!list.isEmpty()) {
        return true;
      }
      for (Object element : list) {
        if (containsNestedList(element)) {
          return true;
        }
      }
    } else if (value instanceof Map<?, ?> map) {
      for (Object nestedValue : map.values()) {
        if (containsNestedList(nestedValue)) {
          return true;
        }
      }
    }
    return false;
  }
}
