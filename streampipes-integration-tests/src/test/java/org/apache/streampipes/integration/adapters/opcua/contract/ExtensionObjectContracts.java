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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public final class ExtensionObjectContracts {

  private ExtensionObjectContracts() {
  }

  public static List<OpcUaNodeContract> all() {
    return List.of(
        new OpcUaNodeContract(
            "ExtensionObject should decode to expected struct values",
            "ns=2;s=Demo.DataTypeTest.ExtensionObject",
            value -> {
              @SuppressWarnings("unchecked")
              Map<String, Object> record = assertInstanceOf(Map.class, value);
              ExtensionObjectAssertions.assertNoDecodeError(record, "ExtensionObject should decode without _decodeError");
              ExtensionObjectAssertions.assertExtensionObjectRecord(record, (short) 0, 0.0d, "", false, null);
            }
        ),
        new OpcUaNodeContract(
            "ExtensionObjectArray should decode to two expected struct values",
            "ns=2;s=Demo.DataTypeTest.ExtensionObjectArray",
            value -> {
              @SuppressWarnings("unchecked")
              List<Object> records = assertInstanceOf(List.class, value);
              assertEquals(2, records.size(), "Expected two ExtensionObject records in ExtensionObjectArray");

              @SuppressWarnings("unchecked")
              Map<String, Object> firstRecord = assertInstanceOf(Map.class, records.get(0));
              ExtensionObjectAssertions.assertNoDecodeError(
                  firstRecord,
                  "First array record should decode without _decodeError"
              );
              ExtensionObjectAssertions.assertExtensionObjectRecord(firstRecord, (short) 0, 0.0d, "", false, null);

              @SuppressWarnings("unchecked")
              Map<String, Object> secondRecord = assertInstanceOf(Map.class, records.get(1));
              ExtensionObjectAssertions.assertNoDecodeError(
                  secondRecord,
                  "Second array record should decode without _decodeError"
              );
              ExtensionObjectAssertions.assertExtensionObjectRecord(secondRecord, (short) 1, 1.0d, "two", true, 42L);
            }
        )
    );
  }
}

