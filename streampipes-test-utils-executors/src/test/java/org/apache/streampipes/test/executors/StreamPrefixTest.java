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

package org.apache.streampipes.test.executors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamPrefixTest {

  @Test
  void s0_AppendsPropertyValueCorrectly() {
    var result = StreamPrefix.s0("testValue");
    assertEquals("s0::testValue", result);
  }

  @Test
  void s1_AppendsPropertyValueCorrectly() {
    var result = StreamPrefix.s1("anotherTestValue");
    assertEquals("s1::anotherTestValue", result);
  }

  @Test
  void s0_HandlesEmptyPropertyValue() {
    var result = StreamPrefix.s0("");
    assertEquals("s0::", result);
  }

  @Test
  void s1_HandlesEmptyPropertyValue() {
    var result = StreamPrefix.s1("");
    assertEquals("s1::", result);
  }

  @Test
  void s0_HandlesSpecialCharactersInPropertyValue() {
    var result = StreamPrefix.s0("value$&*");
    assertEquals("s0::value$&*", result);
  }

  @Test
  void s1_HandlesSpecialCharactersInPropertyValue() {
    var result = StreamPrefix.s1("value@#%");
    assertEquals("s1::value@#%", result);
  }
}