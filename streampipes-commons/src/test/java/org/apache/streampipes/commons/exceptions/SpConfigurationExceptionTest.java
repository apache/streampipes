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
package org.apache.streampipes.commons.exceptions;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SpConfigurationExceptionTest {

  @Test
  public void theSerializableCopyKeepsMessageStackTraceAndEachCauseAsPlainException() {
    var root = new IllegalStateException("unable to find valid certification path");
    var middle = new IOException("Server certificate not trusted", root);
    var e = new SpConfigurationException("Cannot browse 192.168.23.30:443", middle);

    var copy = e.toSerializable();

    assertEquals("Cannot browse 192.168.23.30:443", copy.getMessage());
    assertArrayEquals(e.getStackTrace(), copy.getStackTrace());
    assertSame(Exception.class, copy.getCause().getClass());
    assertEquals("java.io.IOException: Server certificate not trusted", copy.getCause().getMessage());
    assertArrayEquals(middle.getStackTrace(), copy.getCause().getStackTrace());
    assertEquals("java.lang.IllegalStateException: unable to find valid certification path",
        copy.getCause().getCause().getMessage());
    assertNull(copy.getCause().getCause().getCause());
  }

  @Test
  public void aCauseWithoutMessageIsNamedAndACycleEnds() {
    var first = new RuntimeException((String) null);
    var second = new RuntimeException("second", first);
    first.initCause(second);

    var copy = new SpConfigurationException("failed", first).toSerializable();

    assertEquals("java.lang.RuntimeException", copy.getCause().getMessage());
    assertEquals("java.lang.RuntimeException: second", copy.getCause().getCause().getMessage());
    assertNull(copy.getCause().getCause().getCause());
  }

  @Test
  public void anExceptionWithoutCauseStaysWithoutCause() {
    assertNull(new SpConfigurationException("no cause").toSerializable().getCause());
  }
}
