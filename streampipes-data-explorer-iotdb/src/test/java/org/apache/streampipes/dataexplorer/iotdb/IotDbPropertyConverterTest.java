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
package org.apache.streampipes.dataexplorer.iotdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.junit.jupiter.api.Test;

public class IotDbPropertyConverterTest {

  @Test
  public void convertNonPrimitiveProperty() {
    assertThrows(SpRuntimeException.class, () -> new IotDbPropertyConverter().convertNonPrimitiveProperty(null, null));
  }

  @Test
  public void convertPrimitivePropertyInteger() {

    var property = new EventPropertyPrimitive(XSD.INTEGER.toString(), "test", null, null);
    var field = new PrimitiveField("test", "test", 5);

    var result = new IotDbPropertyConverter().convertPrimitiveProperty(property, field, "sanitizedTest");

    assertEquals("sanitizedTest", result.measurementName());
    assertEquals(5, result.value());
    assertEquals(TSDataType.INT32, result.dataType());
  }

  @Test
  public void convertPrimitivePropertyString() {

    var property = new EventPropertyPrimitive(XSD.STRING.toString(), "test", null, null);
    var field = new PrimitiveField("test", "test", "value");

    var result = new IotDbPropertyConverter().convertPrimitiveProperty(property, field, "sanitizedTest");

    assertEquals("sanitizedTest", result.measurementName());
    assertEquals("value", result.value());
    assertEquals(TSDataType.TEXT, result.dataType());
  }

  @Test
  public void convertPrimitivePropertyFloat() {

    var property = new EventPropertyPrimitive(XSD.FLOAT.toString(), "test", null, null);
    var field = new PrimitiveField("test", "test", 0.5f);

    var result = new IotDbPropertyConverter().convertPrimitiveProperty(property, field, "sanitizedTest");

    assertEquals("sanitizedTest", result.measurementName());
    assertEquals(0.5f, result.value());
    assertEquals(TSDataType.FLOAT, result.dataType());
  }

  @Test
  public void convertPrimitivePropertyNumber() {

    var property = new EventPropertyPrimitive(SO.NUMBER, "test", null, null);
    var field = new PrimitiveField("test", "test", 5.24);

    var result = new IotDbPropertyConverter().convertPrimitiveProperty(property, field, "sanitizedTest");

    assertEquals("sanitizedTest", result.measurementName());
    assertEquals(5.24, result.value());
    assertEquals(TSDataType.DOUBLE, result.dataType());
  }

  @Test
  public void convertPrimitivePropertyUnknown() {

    var property = new EventPropertyPrimitive(XSD.ANY_TYPE.toString(), "test", null, null);
    var field = new PrimitiveField("test", "test", 5);

    assertThrows(SpRuntimeException.class,
            () -> new IotDbPropertyConverter().convertPrimitiveProperty(property, field, "sanitizedTest"));
  }
}
