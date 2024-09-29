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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

/**
 * Converts StreamPipes {@link EventProperty} into {@link IotDbMeasurementRecord}s.
 */
public class IotDbPropertyConverter {

  /**
   * Converts a {@link EventPropertyPrimitive } into an {@link IotDbMeasurementRecord}.
   *
   * @param eventPropertyPrimitive
   *          The primitive event property to be converted
   * @param primitiveField
   *          The primitive field containing the property value
   * @param sanitizedRuntimeName
   *          The sanitized name of the property
   * @return An IoTDB measurement record representing the converted property
   */
  public IotDbMeasurementRecord convertPrimitiveProperty(EventPropertyPrimitive eventPropertyPrimitive,
          PrimitiveField primitiveField, String sanitizedRuntimeName) {
    var runtimeType = eventPropertyPrimitive.getRuntimeType();
    Object value;
    TSDataType iotDbType;

    if (XSD.INTEGER.toString().equals(runtimeType)) {
      iotDbType = TSDataType.INT32;
      value = primitiveField.getAsInt();
    } else if (XSD.LONG.toString().equals(runtimeType)) {
      iotDbType = TSDataType.INT64;
      value = primitiveField.getAsLong();
    } else if (XSD.FLOAT.toString().equals(runtimeType)) {
      iotDbType = TSDataType.FLOAT;
      value = primitiveField.getAsFloat();
    } else if (XSD.DOUBLE.toString().equals(runtimeType) || SO.NUMBER.equals(runtimeType)) {
      iotDbType = TSDataType.DOUBLE;
      value = primitiveField.getAsDouble();
    } else if (XSD.BOOLEAN.toString().equals(runtimeType)) {
      iotDbType = TSDataType.BOOLEAN;
      value = primitiveField.getAsBoolean();
    } else if (XSD.STRING.toString().equals(runtimeType)) {
      iotDbType = TSDataType.TEXT;
      value = primitiveField.getAsString();
    } else {
      throw new SpRuntimeException(
              "Unsupported runtime type '%s' - cannot be mapped to a IoTDB data type".formatted(runtimeType));
    }

    return new IotDbMeasurementRecord(sanitizedRuntimeName, iotDbType, value);
  }

  /**
   * Converts a non-primitive event property into an {@link IotDbMeasurementRecord}.
   *
   * @param eventProperty
   *          The non-primitive event property to be converted
   * @param sanitizedRuntimeName
   *          The sanitized name of the property
   * @return An IoTDB measurement record representing the converted property
   * @throws SpRuntimeException
   *           If an error occurs during conversion
   */
  public IotDbMeasurementRecord convertNonPrimitiveProperty(EventProperty eventProperty, String sanitizedRuntimeName)
          throws SpRuntimeException {
    throw new SpRuntimeException(
            "Handling non-primitive event properties is not yet supported " + "when using IoTDB as time series store.");
  }
}
