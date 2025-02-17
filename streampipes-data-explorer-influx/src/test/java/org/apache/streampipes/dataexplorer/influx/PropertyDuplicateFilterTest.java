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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.model.runtime.field.PrimitiveField;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertyDuplicateFilterTest {

  @Test
  public void testDuplicate() {
    var filter = new PropertyDuplicateFilter(true);
    var field = new PrimitiveField(null, null, 1);
    var result = filter.shouldIgnoreField("test", field);

    assertFalse(result);

    field = new PrimitiveField(null, null, 2);
    result = filter.shouldIgnoreField("test", field);
    assertFalse(result);

    field = new PrimitiveField(null, null, 2);
    result = filter.shouldIgnoreField("test1", field);
    assertFalse(result);

    field = new PrimitiveField(null, null, 2);
    result = filter.shouldIgnoreField("test", field);
    assertTrue(result);
  }

  @Test
  public void testDuplicateDeactivated() {
    var filter = new PropertyDuplicateFilter(false);
    var field = new PrimitiveField(null, null, 1);
    var result = filter.shouldIgnoreField("test", field);

    assertFalse(result);

    field = new PrimitiveField(null, null, 1);
    result = filter.shouldIgnoreField("test", field);
    assertFalse(result);
  }
}
