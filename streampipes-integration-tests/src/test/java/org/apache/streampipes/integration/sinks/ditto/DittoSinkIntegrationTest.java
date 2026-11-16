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

package org.apache.streampipes.integration.sinks.ditto;

import org.apache.streampipes.integration.containers.DittoContainerStack;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DittoSinkIntegrationTest {

  private static DittoContainerStack ditto;

  @BeforeAll
  public static void startDitto() {
    ditto = new DittoContainerStack();
    ditto.start();
  }

  @AfterAll
  public static void stopDitto() {
    if (ditto != null) {
      ditto.close();
    }
  }

  @Test
  public void testCreatesThingAndWritesSelectedFields() throws Exception {
    try (var tester = new DittoSinkTester(ditto, false)) {
      tester.run();
    }
  }

  @Test
  public void testAddsFeatureToExistingThingWithoutRemovingOtherFeatures() throws Exception {
    try (var tester = new DittoSinkTester(ditto, true)) {
      tester.run();
    }
  }
}
