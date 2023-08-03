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

package org.apache.streampipes.extensions.connectors.opcua.adapter;

import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpcUaNodeMetadataExtractorTest {

  private final String date = new DateTime(1687762000000L).getJavaDate().toString();

  @Test
  public void testExtractDescription() {
    var description = "test";
    var node = mock(UaNode.class);
    when(node.getDescription()).thenReturn(new LocalizedText(description));
    var extractor = getExtractor(node);

    extractor.extractDescription();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("Description"));
    assertEquals(description, metadata.get("Description"));
  }

  @Test
  public void testExtractDescriptionNull() {
    var node = mock(UaNode.class);
    when(node.getDescription()).thenReturn(null);
    var extractor = getExtractor(node);

    extractor.extractDescription();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("Description"));
    assertEquals("", metadata.get("Description"));
  }

  @Test
  public void testExtractNamespaceIndex() {
    var node = mock(UaNode.class);
    when(node.getNodeId()).thenReturn(new NodeId(1, 1));
    var extractor = getExtractor(node);

    extractor.extractNamespaceIndex();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("NamespaceIndex"));
    assertEquals("1", metadata.get("NamespaceIndex"));
  }

  @Test
  public void testExtractNamespaceIndexNull() {
    var node = mock(UaNode.class);
    when(node.getNodeId()).thenReturn(null);
    var extractor = getExtractor(node);

    extractor.extractNamespaceIndex();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("NamespaceIndex"));
    assertEquals("", metadata.get("NamespaceIndex"));
  }

  @Test
  public void testExtractNodeClass() {
    var node = mock(UaNode.class);
    when(node.getNodeClass()).thenReturn(NodeClass.Variable);
    var extractor = getExtractor(node);

    extractor.extractNodeClass();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("NodeClass"));
    assertEquals("Variable", metadata.get("NodeClass"));
  }

  @Test
  public void testExtractNodeClassNull() {
    var node = mock(UaNode.class);
    when(node.getNodeClass()).thenReturn(null);
    var extractor = getExtractor(node);

    extractor.extractNodeClass();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("NodeClass"));
    assertEquals("", metadata.get("NodeClass"));
  }

  @Test
  public void testExtractBrowseName() {
    var expectedNodeName = "nodeName";
    var node = mock(UaNode.class);
    when(node.getBrowseName()).thenReturn(new QualifiedName(1, expectedNodeName));
    var extractor = getExtractor(node);

    extractor.extractBrowseName();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("BrowseName"));
    assertEquals(expectedNodeName, metadata.get("BrowseName"));
  }

  @Test
  public void testExtractBrowseNameNull() {
    var node = mock(UaNode.class);
    when(node.getBrowseName()).thenReturn(null);
    var extractor = getExtractor(node);

    extractor.extractBrowseName();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("BrowseName"));
    assertEquals("", metadata.get("BrowseName"));
  }

  @Test
  public void testExtractDisplayName() {
    var expectedName = "expectedName";
    var node = mock(UaNode.class);
    when(node.getDisplayName()).thenReturn(new LocalizedText(expectedName));
    var extractor = getExtractor(node);

    extractor.extractDisplayName();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("DisplayName"));
    assertEquals(expectedName, metadata.get("DisplayName"));
  }

  @Test
  public void testExtractDisplayNameNull() {
    var node = mock(UaNode.class);
    when(node.getDisplayName()).thenReturn(null);
    var extractor = getExtractor(node);

    extractor.extractDisplayName();

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("DisplayName"));
    assertEquals("", metadata.get("DisplayName"));
  }

  @Test
  public void testExtractSourceTime() {
    var value = mock(DataValue.class);
    when(value.getSourceTime()).thenReturn(new DateTime(1687762000000L));
    var extractor = getExtractor();

    extractor.extractSourceTime(value);

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("SourceTime"));
    assertEquals(date, metadata.get("SourceTime"));
  }

  @Test
  public void testExtractSourceTimeNull() {
    var value = mock(DataValue.class);
    when(value.getSourceTime()).thenReturn(null);
    var extractor = getExtractor();

    extractor.extractSourceTime(value);

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("SourceTime"));
    assertEquals("", metadata.get("SourceTime"));
  }

  @Test
  public void testExtractServerTime() {
    var value = mock(DataValue.class);
    when(value.getServerTime()).thenReturn(new DateTime(1687762000000L));
    var extractor = getExtractor();

    extractor.extractServerTime(value);

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("ServerTime"));
    assertEquals(date, metadata.get("ServerTime"));
  }

  @Test
  public void testExtractServerTimeNull() {
    var value = mock(DataValue.class);
    when(value.getServerTime()).thenReturn(null);
    var extractor = getExtractor();

    extractor.extractServerTime(value);

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("ServerTime"));
    assertEquals("", metadata.get("ServerTime"));
  }

  @Test
  public void testExtractDataType() {
    var expectedName = "expectedName";
    var dataTypeNode = mock(UaNode.class);
    when(dataTypeNode.getDisplayName()).thenReturn(new LocalizedText(expectedName));
    var extractor = getExtractor();

    extractor.extractDataType(dataTypeNode);

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("DataType"));
    assertEquals(expectedName, metadata.get("DataType"));
  }

  @Test
  public void testExtractDataTypeNull() {
    var dataTypeNode = mock(UaNode.class);
    when(dataTypeNode.getDisplayName()).thenReturn(null);
    var extractor = getExtractor();

    extractor.extractDataType(dataTypeNode);

    var metadata = extractor.getMetadata();
    assertTrue(metadata.containsKey("DataType"));
    assertEquals("", metadata.get("DataType"));
  }

  private OpcUaNodeMetadataExtractor getExtractor(UaNode node) {
    return new OpcUaNodeMetadataExtractor(null, node);
  }

  private OpcUaNodeMetadataExtractor getExtractor() {
    return new OpcUaNodeMetadataExtractor(null, null);
  }
}