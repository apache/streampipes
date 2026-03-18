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

package org.apache.streampipes.model.extensions.transport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionServiceTransportModeTest {

  @Test
  void shouldDefaultToHttpForNullOrBlankInput() {
    assertEquals(ExtensionServiceTransportMode.HTTP, ExtensionServiceTransportMode.from(null));
    assertEquals(ExtensionServiceTransportMode.HTTP, ExtensionServiceTransportMode.from(""));
    assertEquals(ExtensionServiceTransportMode.HTTP, ExtensionServiceTransportMode.from("   "));
  }

  @Test
  void shouldParseModesCaseInsensitive() {
    assertEquals(ExtensionServiceTransportMode.HTTP, ExtensionServiceTransportMode.from("http"));
    assertEquals(ExtensionServiceTransportMode.NATS, ExtensionServiceTransportMode.from("nats"));
    assertEquals(ExtensionServiceTransportMode.DUAL, ExtensionServiceTransportMode.from("Dual"));
  }

  @Test
  void shouldFailFastForInvalidMode() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> ExtensionServiceTransportMode.from("invalid-mode")
    );

    assertTrue(exception.getMessage().contains("invalid-mode"));
  }
}
