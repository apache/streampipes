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
import org.apache.streampipes.integration.containers.OpcUaDemoServerContainer;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpcUaAdapterIntegrationTest {


  private static final OpcUaDemoServerContainer OPC_UA_CONTAINER = new OpcUaDemoServerContainer();

  private final OpcUaAdapterTestHarness harness = new OpcUaAdapterTestHarness();

  @BeforeAll
  public static void startContainer() {
    OPC_UA_CONTAINER.start();
  }

  @AfterAll
  public static void stopContainer() {
    OPC_UA_CONTAINER.stop();
  }

  @Test
  public void testScalarBooleanNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Boolean",
        Map.of("Boolean", false)
    );
  }

  @Test
  public void testScalarDoubleNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Double",
        Map.of("Double", 0.0d)
    );
  }

  @Test
  public void testScalarByteNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Byte",
        Map.of("Byte", UByte.valueOf(0))
    );
  }

  @Test
  public void testScalarByteStringNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.ByteString",
        Map.of("ByteString", "AQIDBA==")
    );
  }

  @Test
  public void testScalarDataValueNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Scalar.DataValue")
    );

    assertEquals(1, event.size());
    var dataValue = assertInstanceOf(Map.class, event.get("DataValue"));

    assertEquals(42, dataValue.get("value"));
    assertEquals("Good (0)", dataValue.get("statusCode"));

    var sourceTimestamp = Instant.parse(assertInstanceOf(String.class, dataValue.get("sourceTimestamp")));
    var serverTimestamp = Instant.parse(assertInstanceOf(String.class, dataValue.get("serverTimestamp")));

    assertNotNull(sourceTimestamp);
    assertNotNull(serverTimestamp);
    assertEquals(sourceTimestamp, serverTimestamp);
  }

  @Test
  public void testScalarDateTimeNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Scalar.DateTime")
    );

    assertEquals(1, event.size());
    assertTrue(assertInstanceOf(Long.class, event.get("DateTime")) > 0L);
  }

  @Test
  public void testScalarExpandedNodeIdNode() throws Exception {
    var expectedExpandedNodeId = new LinkedHashMap<String, Object>();
    expectedExpandedNodeId.put("identifier", "DoesNotExist");
    expectedExpandedNodeId.put("namespaceIndex", null);
    expectedExpandedNodeId.put("type", "String");
    expectedExpandedNodeId.put("namespaceUri", "urn:opc:eclipse:milo:opc-ua-demo-server:namespace:demo");
    expectedExpandedNodeId.put("serverIndex", 0L);

    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.ExpandedNodeId",
        Map.of("ExpandedNodeId", expectedExpandedNodeId)
    );
  }

  @Test
  public void testScalarExtensionObjectNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Scalar.ExtensionObject")
    );

    assertEquals(1, event.size());
    var extensionObject = assertInstanceOf(Map.class, event.get("ExtensionObject"));
    assertEquals(1.0d, ((Number) extensionObject.get("X")).doubleValue());
    assertEquals(2.0d, ((Number) extensionObject.get("Value")).doubleValue());
  }

  @Test
  public void testScalarFloatNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Float",
        Map.of("Float", 0.0f)
    );
  }

  @Test
  public void testScalarGuidNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Scalar.Guid")
    );

    assertEquals(1, event.size());
    var guid = assertInstanceOf(UUID.class, event.get("Guid"));
    assertTrue(
        guid.toString()
            .matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    );
  }

  @Test
  public void testScalarInt16Node() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Int16",
        Map.of("Int16", (short) 0)
    );
  }

  @Test
  public void testScalarInt32Node() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Int32",
        Map.of("Int32", 0)
    );
  }

  @Test
  public void testScalarInt64Node() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Int64",
        Map.of("Int64", 0L)
    );
  }

  @Test
  public void testScalarIntegerNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Integer",
        Map.of("Integer", Integer.MIN_VALUE)
    );
  }

  @Test
  public void testScalarLocalizedTextNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Scalar.LocalizedText")
    );

    assertEquals(1, event.size());
    assertEquals(
        "hello",
        assertInstanceOf(LocalizedText.class, event.get("LocalizedText")).getText()
    );
  }

  @Test
  public void testScalarNodeIdNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Scalar.NodeId")
    );

    assertEquals(1, event.size());
    assertEquals(
        "ns=1;s=DoesNotExist",
        assertInstanceOf(NodeId.class, event.get("NodeId")).toParseableString()
    );
  }

  @Test
  public void testScalarQualifiedNameNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Scalar.QualifiedName")
    );

    assertEquals(1, event.size());
    assertEquals(
        "QualifiedName",
        assertInstanceOf(QualifiedName.class, event.get("QualifiedName")).getName()
    );
  }

  @Test
  public void testScalarSByteNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.SByte",
        Map.of("SByte", (byte) 0)
    );
  }

  @Test
  public void testScalarStatusCodeNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Scalar.StatusCode")
    );

    assertEquals(
        Map.of("StatusCode", StatusCode.GOOD),
        event
    );
  }

  @Test
  public void testScalarStringNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.String",
        Map.of("String", "hello")
    );
  }

  @Test
  public void testScalarUInt16Node() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.UInt16",
        Map.of("UInt16", UShort.valueOf(0))
    );
  }

  @Test
  public void testScalarUInt32Node() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.UInt32",
        Map.of("UInt32", UInteger.valueOf(0L))
    );
  }

  @Test
  public void testScalarUInt64Node() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.UInt64",
        Map.of("UInt64", ULong.valueOf(0L))
    );
  }

  @Test
  public void testScalarUIntegerNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.UInteger",
        Map.of("UInteger", 0L)
    );
  }

  @Test
  public void testScalarVariantNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.Variant",
        Map.of("Variant", 42)
    );
  }

  @Test
  public void testScalarXmlElementNode() throws Exception {
    assertSingleNodeEvent(
        "ns=2;s=CTT.Static.AllProfiles.Scalar.XmlElement",
        Map.of("XmlElement", "<xml></xml>")
    );
  }

  @Test
  public void testBooleanArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.BooleanArray",
        "BooleanArray",
        repeated(false)
    );
  }

  @Test
  public void testByteArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.ByteArray",
        "ByteArray",
        repeated(UByte.valueOf(0))
    );
  }

  @Test
  public void testByteStringArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.ByteStringArray",
        "ByteStringArray",
        repeated("AQIDBA==")
    );
  }

  @Test
  public void testDataValueArrayNode() throws Exception {
    var dataValues = assertSingleNodeArrayValue(
        "ns=2;s=CTT.Static.AllProfiles.Array.DataValueArray",
        "DataValueArray"
    );

    assertEquals(5, dataValues.length);
    for (var value : dataValues) {
      var dataValue = assertInstanceOf(DataValue.class, value);
      assertEquals(new Variant(42), dataValue.getValue());
      assertEquals(StatusCode.GOOD, dataValue.getStatusCode());
      assertNotNull(dataValue.getSourceTime());
      assertNotNull(dataValue.getServerTime());
    }
  }

  @Test
  public void testDateTimeArrayNode() throws Exception {
    var dateTimes = assertSingleNodeArrayValue(
        "ns=2;s=CTT.Static.AllProfiles.Array.DateTimeArray",
        "DateTimeArray"
    );

    assertEquals(5, dateTimes.length);
    for (var value : dateTimes) {
      assertTrue(assertInstanceOf(DateTime.class, value).getJavaTime() > 0L);
    }
  }

  @Test
  public void testDoubleArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.DoubleArray",
        "DoubleArray",
        repeated(0.0d)
    );
  }

  @Test
  public void testExpandedNodeIdArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.ExpandedNodeIdArray",
        "ExpandedNodeIdArray",
        repeated(ExpandedNodeId.parse("nsu=urn:opc:eclipse:milo:opc-ua-demo-server:namespace:demo;s=DoesNotExist"))
    );
  }

  @Test
  public void testExtensionObjectArrayNodeFromCtt() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=CTT.Static.AllProfiles.Array.ExtensionObjectArray")
    );

    assertEquals(1, event.size());
    var extensionObjects = assertInstanceOf(List.class, event.get("ExtensionObjectArray"));
    assertEquals(5, extensionObjects.size());

    for (var value : extensionObjects) {
      var extensionObject = assertInstanceOf(Map.class, value);
      assertEquals(1.0d, ((Number) extensionObject.get("X")).doubleValue());
      assertEquals(2.0d, ((Number) extensionObject.get("Value")).doubleValue());
    }
  }

  @Test
  public void testFloatArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.FloatArray",
        "FloatArray",
        repeated(0.0f)
    );
  }

  @Test
  public void testGuidArrayNode() throws Exception {
    var guids = assertSingleNodeArrayValue(
        "ns=2;s=CTT.Static.AllProfiles.Array.GuidArray",
        "GuidArray"
    );

    assertEquals(5, guids.length);
    for (var value : guids) {
      assertTrue(
          assertInstanceOf(UUID.class, value).toString()
              .matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
      );
    }
  }

  @Test
  public void testInt16ArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.Int16Array",
        "Int16Array",
        repeated((short) 0)
    );
  }

  @Test
  public void testInt32ArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.Int32Array",
        "Int32Array",
        repeated(0)
    );
  }

  @Test
  public void testInt64ArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.Int64Array",
        "Int64Array",
        repeated(0L)
    );
  }

  @Test
  public void testLocalizedTextArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.LocalizedTextArray",
        "LocalizedTextArray",
        repeated(new LocalizedText("en", "hello"))
    );
  }

  @Test
  public void testNodeIdArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.NodeIdArray",
        "NodeIdArray",
        repeated(NodeId.parse("ns=1;s=DoesNotExist"))
    );
  }

  @Test
  public void testQualifiedNameArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.QualifiedNameArray",
        "QualifiedNameArray",
        repeated(new QualifiedName(1, "QualifiedName"))
    );
  }

  @Test
  public void testSByteArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.SByteArray",
        "SByteArray",
        repeated((byte) 0)
    );
  }

  @Test
  public void testStatusCodeArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.StatusCodeArray",
        "StatusCodeArray",
        repeated(StatusCode.GOOD)
    );
  }

  @Test
  public void testStringArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.StringArray",
        "StringArray",
        repeated("hello")
    );
  }

  @Test
  public void testUInt16ArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.UInt16Array",
        "UInt16Array",
        repeated(UShort.valueOf(0))
    );
  }

  @Test
  public void testUInt32ArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.UInt32Array",
        "UInt32Array",
        repeated(UInteger.valueOf(0L))
    );
  }

  @Test
  public void testUInt64ArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.UInt64Array",
        "UInt64Array",
        repeated(ULong.valueOf(0L))
    );
  }

  @Test
  public void testVariantArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.VariantArray",
        "VariantArray",
        repeated(new Variant(42))
    );
  }

  @Test
  public void testXmlElementArrayNode() throws Exception {
    assertSingleNodeObjectArrayEvent(
        "ns=2;s=CTT.Static.AllProfiles.Array.XmlElementArray",
        "XmlElementArray",
        repeated(new XmlElement("<xml></xml>"))
    );
  }

  @Test
  public void testBooleanMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.BooleanMatrix",
        "BooleanMatrix",
        repeatedMatrix(false)
    );
  }

  @Test
  public void testByteMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.ByteMatrix",
        "ByteMatrix",
        repeatedMatrix(UByte.valueOf(0))
    );
  }

  @Test
  public void testByteStringMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.ByteStringMatrix",
        "ByteStringMatrix",
        repeatedMatrix("AQIDBA==")
    );
  }

  @Test
  public void testDataValueMatrixNode() throws Exception {
    var matrix = assertSingleNodeMatrixValue(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.DataValueMatrix",
        "DataValueMatrix"
    );

    assertEquals(List.of(5, 5), matrix.get("dimensions"));

    var elements = assertInstanceOf(List.class, matrix.get("elements"));
    assertEquals(25, elements.size());
    for (var value : elements) {
      var dataValue = assertInstanceOf(Map.class, value);
      assertEquals(42, dataValue.get("value"));
      assertEquals("Good (0)", dataValue.get("statusCode"));
      assertNotNull(Instant.parse(assertInstanceOf(String.class, dataValue.get("sourceTimestamp"))));
      assertNotNull(Instant.parse(assertInstanceOf(String.class, dataValue.get("serverTimestamp"))));
    }
  }

  @Test
  public void testDateTimeMatrixNode() throws Exception {
    var matrix = assertSingleNodeMatrixValue(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.DateTimeMatrix",
        "DateTimeMatrix"
    );

    assertEquals(List.of(5, 5), matrix.get("dimensions"));

    var elements = assertInstanceOf(List.class, matrix.get("elements"));
    assertEquals(25, elements.size());
    for (var value : elements) {
      assertTrue(assertInstanceOf(Long.class, value) > 0L);
    }
  }

  @Test
  public void testDoubleMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.DoubleMatrix",
        "DoubleMatrix",
        repeatedMatrix(0.0d)
    );
  }

  @Test
  public void testExpandedNodeIdMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.ExpandedNodeIdMatrix",
        "ExpandedNodeIdMatrix",
        repeatedMatrix(expandedNodeIdValue())
    );
  }

  @Test
  public void testExtensionObjectMatrixNode() throws Exception {
    var matrix = assertSingleNodeMatrixValue(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.ExtensionObjectMatrix",
        "ExtensionObjectMatrix"
    );

    assertEquals(List.of(5, 5), matrix.get("dimensions"));

    var elements = assertInstanceOf(List.class, matrix.get("elements"));
    assertEquals(25, elements.size());
    for (var value : elements) {
      assertInstanceOf(ExtensionObject.class, value);
    }
  }

  @Test
  public void testFloatMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.FloatMatrix",
        "FloatMatrix",
        repeatedMatrix(0.0f)
    );
  }

  @Test
  public void testGuidMatrixNode() throws Exception {
    var matrix = assertSingleNodeMatrixValue(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.GuidMatrix",
        "GuidMatrix"
    );

    assertEquals(List.of(5, 5), matrix.get("dimensions"));

    var elements = assertInstanceOf(List.class, matrix.get("elements"));
    assertEquals(25, elements.size());
    for (var value : elements) {
      assertTrue(
          assertInstanceOf(UUID.class, value).toString()
              .matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
      );
    }
  }

  @Test
  public void testInt16MatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.Int16Matrix",
        "Int16Matrix",
        repeatedMatrix((short) 0)
    );
  }

  @Test
  public void testInt32MatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.Int32Matrix",
        "Int32Matrix",
        repeatedMatrix(0)
    );
  }

  @Test
  public void testInt64MatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.Int64Matrix",
        "Int64Matrix",
        repeatedMatrix(0L)
    );
  }

  @Test
  public void testLocalizedTextMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.LocalizedTextMatrix",
        "LocalizedTextMatrix",
        repeatedMatrix(new LocalizedText("en", "hello"))
    );
  }

  @Test
  public void testNodeIdMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.NodeIdMatrix",
        "NodeIdMatrix",
        repeatedMatrix(NodeId.parse("ns=1;s=DoesNotExist"))
    );
  }

  @Test
  public void testQualifiedNameMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.QualifiedNameMatrix",
        "QualifiedNameMatrix",
        repeatedMatrix(new QualifiedName(1, "QualifiedName"))
    );
  }

  @Test
  public void testSByteMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.SByteMatrix",
        "SByteMatrix",
        repeatedMatrix((byte) 0)
    );
  }

  @Test
  public void testStatusCodeMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.StatusCodeMatrix",
        "StatusCodeMatrix",
        repeatedMatrix(StatusCode.GOOD)
    );
  }

  @Test
  public void testStringMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.StringMatrix",
        "StringMatrix",
        repeatedMatrix("hello")
    );
  }

  @Test
  public void testUInt16MatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.UInt16Matrix",
        "UInt16Matrix",
        repeatedMatrix(UShort.valueOf(0))
    );
  }

  @Test
  public void testUInt32MatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.UInt32Matrix",
        "UInt32Matrix",
        repeatedMatrix(UInteger.valueOf(0L))
    );
  }

  @Test
  public void testUInt64MatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.UInt64Matrix",
        "UInt64Matrix",
        repeatedMatrix(ULong.valueOf(0L))
    );
  }

  @Test
  public void testVariantMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.VariantMatrix",
        "VariantMatrix",
        repeatedMatrix(new Variant(42))
    );
  }

  @Test
  public void testXmlElementMatrixNode() throws Exception {
    assertSingleNodeMatrixEvent(
        "ns=2;s=CTT.Static.AllProfiles.Matrix.XmlElementMatrix",
        "XmlElementMatrix",
        repeatedMatrix("<xml></xml>")
    );
  }

  @Test
  public void testExtensionObjectNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=Demo.DataTypeTest.ExtensionObject")
    );

    assertEquals(
        Map.of("ExtensionObject", EMPTY_EXTENSION_OBJECT),
        event
    );
  }

  @Test
  public void testExtensionObjectArrayNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=Demo.DataTypeTest.ExtensionObjectArray")
    );

    assertEquals(
        Map.of(
            "ExtensionObjectArray",
            List.of(
                Map.of(
                    "Int16Field", (short) 0,
                    "DoubleField", 0.0d,
                    "StringField", "",
                    "BooleanField", false
                ),
                Map.of(
                    "Int16Field", (short) 1,
                    "DoubleField", 1.0d,
                    "StringField", "two",
                    "BooleanField", true,
                    "UInt32Field", UInteger.valueOf(42L)
                )
            )
        ),
        event
    );
  }

  @Test
  public void testConcreteTestTypeNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=Demo.DataTypeTest.ConcreteTestType")
    );

    assertEquals(
        Map.of("ConcreteTestType", EMPTY_EXTENSION_OBJECT),
        event
    );
  }

  @Test
  public void testStructWithOptionalMatrixFieldsNode() throws Exception {
    var event = readSingleEvent(
        List.of("ns=2;s=StructWithOptionalMatrixFields")
    );

    assertEquals(
        Map.of(
            "StructWithOptionalMatrixFields",
            Map.of(
                "Int32", matrixValue(0, 0, 0, 0),
                "OptionalInt32", matrixValue(0, 0, 0, 0),
                "String", matrixValue("", "", "", ""),
                "OptionalString", matrixValue("", "", "", ""),
                "Duration", matrixValue(0.0d, 0.0d, 0.0d, 0.0d),
                "OptionalDuration", matrixValue(0.0d, 0.0d, 0.0d, 0.0d),
                "ConcreteTestType", matrixValue(
                    EMPTY_EXTENSION_OBJECT,
                    EMPTY_EXTENSION_OBJECT,
                    EMPTY_EXTENSION_OBJECT,
                    EMPTY_EXTENSION_OBJECT
                ),
                "OptionalConcreteTestType", matrixValue(
                    EMPTY_EXTENSION_OBJECT,
                    EMPTY_EXTENSION_OBJECT,
                    EMPTY_EXTENSION_OBJECT,
                    EMPTY_EXTENSION_OBJECT
                )
            )
        ),
        event
    );
  }

  private Map<String, Object> readSingleEvent(List<String> nodeIds) throws Exception {
    return harness.readSingleEvent(OPC_UA_CONTAINER.getEndpointUrl(), nodeIds);
  }

  private void assertSingleNodeEvent(String nodeId,
                                     Map<String, Object> expectedEvent) throws Exception {
    var event = readSingleEvent(List.of(nodeId));
    assertEquals(expectedEvent, event);
  }

  private Object[] assertSingleNodeArrayValue(String nodeId,
                                              String fieldName) throws Exception {
    var event = readSingleEvent(List.of(nodeId));
    assertEquals(1, event.size());
    return assertInstanceOf(Object[].class, event.get(fieldName));
  }

  private void assertSingleNodeObjectArrayEvent(String nodeId,
                                                String fieldName,
                                                Object[] expectedArray) throws Exception {
    assertArrayEquals(expectedArray, assertSingleNodeArrayValue(nodeId, fieldName));
  }

  private Object[] repeated(Object value) {
    return new Object[] {value, value, value, value, value};
  }

  private Map<String, Object> assertSingleNodeMatrixValue(String nodeId,
                                                          String fieldName) throws Exception {
    var event = readSingleEvent(List.of(nodeId));
    assertEquals(1, event.size());
    return assertInstanceOf(Map.class, event.get(fieldName));
  }

  private void assertSingleNodeMatrixEvent(String nodeId,
                                           String fieldName,
                                           Object[] expectedFlatArray) throws Exception {
    var matrix = assertSingleNodeMatrixValue(nodeId, fieldName);
    assertEquals(List.of(5, 5), matrix.get("dimensions"));
    assertEquals(Arrays.asList(expectedFlatArray), matrix.get("elements"));
  }

  private Object[] repeatedMatrix(Object value) {
    var values = new Object[25];
    for (int i = 0; i < values.length; i++) {
      values[i] = value;
    }
    return values;
  }

  private Map<String, Object> expandedNodeIdValue() {
    var expandedNodeId = new LinkedHashMap<String, Object>();
    expandedNodeId.put("identifier", "DoesNotExist");
    expandedNodeId.put("namespaceIndex", null);
    expandedNodeId.put("type", "String");
    expandedNodeId.put("namespaceUri", "urn:opc:eclipse:milo:opc-ua-demo-server:namespace:demo");
    expandedNodeId.put("serverIndex", 0L);
    return expandedNodeId;
  }

  private Map<String, Object> matrixValue(Object... elements) {
    return Map.of(
        "elements", List.of(elements),
        "dimensions", List.of(2, 2)
    );
  }

  private static final Map<String, Object> EMPTY_EXTENSION_OBJECT = Map.of(
      "Int16Field", (short) 0,
      "DoubleField", 0.0d,
      "StringField", "",
      "BooleanField", false
  );
}
