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

package org.apache.streampipes.integration.adapters.opcua.contract;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class AdditionalNodeContracts {

  private AdditionalNodeContracts() {
  }

  public static List<OpcUaNodeContract> all() {
    return List.of(
        new OpcUaNodeContract(
            "Scalar Double should be decoded as Number with expected value",
            "ns=2;s=CTT.Static.AllProfiles.Scalar.Double",
            value -> {
              Number number = assertInstanceOf(Number.class, value);
              assertEquals(0.0d, number.doubleValue(), "Unexpected value for CTT static scalar double");
            }
        ),
        new OpcUaNodeContract(
            "ConcreteTestType should decode to expected struct values",
            "ns=2;s=Demo.DataTypeTest.ConcreteTestType",
            value -> {
              @SuppressWarnings("unchecked")
              Map<String, Object> record = assertInstanceOf(Map.class, value);
              ExtensionObjectAssertions.assertNoDecodeError(
                  record,
                  "ConcreteTestType should decode without _decodeError"
              );
              ExtensionObjectAssertions.assertExtensionObjectRecord(record, (short) 0, 0.0d, "", false, null);
            }
        ),
        new OpcUaNodeContract(
            "StructWithOptionalMatrixFields should decode to clean map representation",
            "ns=2;s=StructWithOptionalMatrixFields",
            value -> {
              @SuppressWarnings("unchecked")
              Map<String, Object> record = assertInstanceOf(Map.class, value);
              ExtensionObjectAssertions.assertNoDecodeError(
                  record,
                  "StructWithOptionalMatrixFields should decode without _decodeError"
              );
              assertFalse(
                  containsMetadataKeyDeep(record, "_decodeError"),
                  "StructWithOptionalMatrixFields contains nested _decodeError metadata: " + record
              );
              assertFalse(
                  containsMetadataKeyDeep(record, "_javaType"),
                  "StructWithOptionalMatrixFields contains Java type metadata in payload: " + record
              );
              assertTrue(!record.isEmpty(), "StructWithOptionalMatrixFields should not decode to an empty map");
            }
        )
    );
  }

  private static boolean containsMetadataKeyDeep(Object value, String key) {
    if (value instanceof Map<?, ?> map) {
      if (map.containsKey(key)) {
        return true;
      }
      for (Object nested : map.values()) {
        if (containsMetadataKeyDeep(nested, key)) {
          return true;
        }
      }
      return false;
    }

    if (value instanceof Iterable<?> iterable) {
      for (Object element : iterable) {
        if (containsMetadataKeyDeep(element, key)) {
          return true;
        }
      }
      return false;
    }

    return false;
  }
}
