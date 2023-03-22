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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state;

import org.apache.streampipes.sdk.helpers.Tuple3;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class TestStringToStateProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestStringToStateProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"Test", List.of("t1"), "t1"},
        {"Test", Arrays.asList("t1", "t2"), "t2"},
        {"Test", Arrays.asList("t1", "t2", "t1", "t3"), "t3"}
    });
  }

  @org.junit.runners.Parameterized.Parameter
  public String selectedFieldName;

  @org.junit.runners.Parameterized.Parameter(1)
  public List<String> eventStrings;

  @org.junit.runners.Parameterized.Parameter(2)
  public Tuple3<String, String, Integer> expectedValue;

  @Test
  public void testStringToState() {
  }
}
