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
package org.apache.streampipes.manager.selector;

import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestPropertyRenaming {

  @Test
  public void testRenaming() {
    EventSchema schema1 = TestSelectorUtils.makeSimpleSchema();
    EventSchema schema2 = TestSelectorUtils.makeSimpleSchema();

    List<String> propertySelectors = Arrays.asList("s0::timestamp", "s1::timestamp");

    Tuple2<List<EventProperty>, List<PropertyRenameRule>> properties =
        new PropertySelector(schema1, schema2).createRenamedPropertyList(propertySelectors);

    assertEquals(properties.k.size(), 2);
  }
}
