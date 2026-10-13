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

package org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.GenericPlc4xAdapter;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionExtractor;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcBrowseItem;
import org.apache.plc4x.java.api.messages.PlcBrowseRequest;
import org.apache.plc4x.java.api.messages.PlcBrowseResponse;
import org.apache.plc4x.java.api.metadata.PlcConnectionMetadata;
import org.apache.plc4x.java.api.model.PlcTag;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.BROWSE_QUERY;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.BROWSE_RESULTS;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.PLC_IP;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.TAG_INPUT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlcBrowseServiceTest {

  private PlcConnectionManager factory;
  private PlcConnection connection;
  private PlcConnectionMetadata metadata;
  private PlcBrowseRequest request;
  private PlcBrowseResponse response;
  private PlcBrowseService service;

  @BeforeEach
  void setup() throws Exception {
    factory = mock(PlcConnectionManager.class);
    connection = mock(PlcConnection.class);
    metadata = mock(PlcConnectionMetadata.class);
    var builder = mock(PlcBrowseRequest.Builder.class);
    request = mock(PlcBrowseRequest.class);
    response = mock(PlcBrowseResponse.class);
    when(factory.getConnection("plc://host")).thenReturn(connection);
    when(connection.getMetadata()).thenReturn(metadata);
    when(metadata.isBrowseSupported()).thenReturn(true);
    when(connection.browseRequestBuilder()).thenReturn(builder);
    when(builder.addQuery("tags", "*")).thenReturn(builder);
    when(builder.build()).thenReturn(request);
    doReturn(CompletableFuture.completedFuture(response)).when(request).execute();
    when(response.getResponseCode("tags")).thenReturn(PlcResponseCode.OK);
    service = new PlcBrowseService(factory);
  }

  @Test
  void preservesHierarchyAndExactReadableAddressesAndReleasesLease() throws Exception {
    var child = item("Temperature", "%DB1:0:REAL", true);
    var folder = item("DB1", null, false);
    when(folder.getChildren()).thenReturn(Map.of("temperature", child));
    var writeOnly = item("Command", "%DB1:4:BOOL", false);
    when(response.getValues("tags")).thenReturn(List.of(folder, writeOnly));
    var nodes = service.browse("plc://host", "*");
    assertEquals("Command", nodes.getFirst().getNodeName());
    assertFalse(nodes.getFirst().isDataNode());
    assertFalse(nodes.get(1).isDataNode());
    var leaf = nodes.get(1).getChildren().getFirst();
    assertTrue(leaf.isDataNode());
    assertEquals("%DB1:0:REAL", leaf.getInternalNodeName());
    verify(connection).close();
  }

  @Test
  void rejectsUnsupportedConnectionsWithoutSendingARequest() throws Exception {
    when(metadata.isBrowseSupported()).thenReturn(false);
    assertThrows(SpConfigurationException.class, () -> service.browse("plc://host", "*"));
    verify(connection, never()).browseRequestBuilder();
    verify(connection).close();
  }

  @Test
  void reportsProtocolFailureAndReleasesLease() throws Exception {
    when(response.getResponseCode("tags")).thenReturn(PlcResponseCode.UNSUPPORTED);
    var error = assertThrows(SpConfigurationException.class, () -> service.browse("plc://host", "*"));
    assertTrue(error.getMessage().contains("UNSUPPORTED"));
    verify(connection).close();
  }

  @Test
  void boundsBrowseWaitAndReleasesLeaseOnTimeout() throws Exception {
    var future = mock(CompletableFuture.class);
    when(future.get(10, TimeUnit.SECONDS)).thenThrow(new TimeoutException());
    doReturn(future).when(request).execute();
    assertThrows(SpConfigurationException.class, () -> service.browse("plc://host", "*"));
    verify(connection).close();
  }

  @Test
  void doesNotExposeDriverCredentialsInFailure() throws Exception {
    doReturn(CompletableFuture.failedFuture(new IllegalStateException("password=secret"))).when(request).execute();
    var error = assertThrows(SpConfigurationException.class, () -> service.browse("plc://host", "*"));
    assertFalse(error.toString().contains("secret"));
    assertEquals(null, error.getCause());
    verify(connection).close();
  }

  @Test
  void resolvesRootAndExpandedNodesWithoutConfiguredReadTags() throws Exception {
    var child = item("Temperature", "%DB1:0:REAL", true);
    var folder = item("DB1", null, false);
    when(folder.getChildren()).thenReturn(Map.of("temperature", child));
    when(response.getValues("tags")).thenReturn(List.of(folder));
    when(factory.getConnection("s7:cotp://localhost")).thenReturn(connection);
    var adapter = new GenericPlc4xAdapter(PlcDriverManager.getDefault().getDriver("s7"), factory);
    var extractor = StaticPropertyExtractor.from(adapter.declareConfig().getAdapterDescription().getConfig(), List.of());
    extractor.getStaticPropertyByName(PLC_IP, FreeTextStaticProperty.class).setValue("localhost");
    var alternatives = extractor.getStaticPropertyByName(TAG_INPUT, StaticPropertyAlternatives.class);
    alternatives.getAlternatives().getFirst().setSelected(false);
    alternatives.getAlternatives().get(1).setSelected(true);
    var tree = (RuntimeResolvableTreeInputStaticProperty) adapter.resolveConfiguration(BROWSE_RESULTS, extractor);
    assertEquals(1, tree.getNodes().size());
    assertTrue(tree.getSelectedNodesInternalNames().isEmpty());
    tree.setNextBaseNodeToResolve(tree.getNodes().getFirst().getInternalNodeName());
    adapter.resolveConfiguration(BROWSE_RESULTS, extractor);
    assertEquals("%DB1:0:REAL", tree.getLatestFetchedNodes().getFirst().getInternalNodeName());
    tree.setNextBaseNodeToResolve("%DB1:0:REAL");
    adapter.resolveConfiguration(BROWSE_RESULTS, extractor);
    assertTrue(tree.getLatestFetchedNodes().isEmpty());
  }

  @Test
  void opcuaUsesObjectsFolderAndRetainsEndpointPathForOldWildcardQueries() throws Exception {
    var builder = connection.browseRequestBuilder();
    when(builder.addQuery("tags", "ns=0;i=85")).thenReturn(builder);
    when(response.getValues("tags")).thenReturn(List.of());
    when(factory.getConnection("opcua:tcp://localhost:4840/milo?browse-max-depth=1")).thenReturn(connection);
    var adapter = new GenericPlc4xAdapter(PlcDriverManager.getDefault().getDriver("opcua"), factory);
    var extractor = StaticPropertyExtractor.from(adapter.declareConfig().getAdapterDescription().getConfig(), List.of());
    extractor.getStaticPropertyByName(PLC_IP, FreeTextStaticProperty.class).setValue("localhost:4840/milo");
    var alternatives = extractor.getStaticPropertyByName(TAG_INPUT, StaticPropertyAlternatives.class);
    alternatives.getAlternatives().getFirst().setSelected(false);
    alternatives.getAlternatives().get(1).setSelected(true);
    assertEquals("ns=0;i=85", extractor.textParameter(BROWSE_QUERY));
    extractor.getStaticPropertyByName(BROWSE_QUERY, FreeTextStaticProperty.class).setValue("*");
    adapter.resolveConfiguration(BROWSE_RESULTS, extractor);
    verify(builder).addQuery("tags", "ns=0;i=85");
    verify(factory).getConnection("opcua:tcp://localhost:4840/milo?browse-max-depth=1");
    var tree = extractor.getStaticPropertyByName(BROWSE_RESULTS, RuntimeResolvableTreeInputStaticProperty.class);
    var parent = "ns=2;s=Demo.Industrial.CncMachine.RawData";
    var address = parent + ".cycleActive;BOOL";
    tree.setNextBaseNodeToResolve(parent);
    when(builder.addQuery("tags", parent)).thenReturn(builder);
    var cycleActive = item("cycleActive", address, true);
    when(response.getValues("tags")).thenReturn(List.of(cycleActive));
    adapter.resolveConfiguration(BROWSE_RESULTS, extractor);
    verify(builder).addQuery("tags", parent);
    assertEquals(address, tree.getLatestFetchedNodes().getFirst().getInternalNodeName());
    assertTrue(tree.getLatestFetchedNodes().getFirst().isDataNode());
    assertEquals("opcua:tcp://localhost:4840/milo",
        new Plc4xConnectionExtractor(extractor, "opcua").makeConnectionString());
    verify(connection, times(2)).close();
  }

  @Test
  void emptyBrowseResultIsSuccessful() throws Exception {
    when(response.getValues("tags")).thenReturn(List.of());
    assertTrue(service.browse("plc://host", "*").isEmpty());
    verify(connection).close();
  }

  private PlcBrowseItem item(String name, String address, boolean readable) {
    var item = mock(PlcBrowseItem.class);
    when(item.getName()).thenReturn(name);
    when(item.isReadable()).thenReturn(readable);
    when(item.getChildren()).thenReturn(Map.of());
    if (address != null) {
      var tag = mock(PlcTag.class);
      when(tag.getAddressString()).thenReturn(address);
      when(item.getTag()).thenReturn(tag);
    }
    return item;
  }
}
