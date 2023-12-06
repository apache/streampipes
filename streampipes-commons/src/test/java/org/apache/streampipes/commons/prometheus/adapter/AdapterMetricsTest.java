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

package org.apache.streampipes.commons.prometheus.adapter;

import org.junit.Test;

import java.util.NoSuchElementException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertThrows;

public class AdapterMetricsTest {

  @Test
  public void register() {

    var metrics = new AdapterMetrics();
    var adapterId = "adapterId";
    var adapterName = "adapterName";

    assertEquals(0, metrics.size());

    metrics.register(adapterId, adapterName);

    assertEquals(1, metrics.size());
    assertTrue(metrics.contains(adapterId));
  }

  @Test
  public void removeNoSuchElement() {
    var metrics = new AdapterMetrics();

    assertThrows(NoSuchElementException.class, () -> metrics.remove("test-id", "test-name"));
  }

  @Test
  public void updateTotalEventsPublishedNoSuchElement() {
    var metrics = new AdapterMetrics();

    assertThrows(
        NoSuchElementException.class,
        () -> metrics.updateTotalEventsPublished("test-id", "test-name", 0)
    );

  }
}
