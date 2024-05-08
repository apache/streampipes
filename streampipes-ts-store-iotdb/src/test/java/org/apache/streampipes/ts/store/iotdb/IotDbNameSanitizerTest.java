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

package org.apache.streampipes.ts.store.iotdb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IotDbNameSanitizerTest {

  @Test
  public void renameReservedKeywords() {
    var sanitizer = new IotDbNameSanitizer();

    assertEquals("runtimeName", sanitizer.renameReservedKeywords("runtimeName"));
    assertEquals("CREATE_", sanitizer.renameReservedKeywords("CREATE"));
    assertEquals("create_", sanitizer.renameReservedKeywords("create"));
    assertEquals("Create_", sanitizer.renameReservedKeywords("Create"));
    assertEquals("CREATE1", sanitizer.renameReservedKeywords("CREATE1"));
  }
}
