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
package org.apache.streampipes.connect.shared.preprocessing;

import org.apache.streampipes.connect.shared.preprocessing.utils.Utils;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {

  @Test
  public void testSortByPriority() {
    var renameTransformationRule = new RenameRuleDescription();
    var addTimestampTransformationRule = new AddTimestampRuleDescription();

    var original = List.of(renameTransformationRule, addTimestampTransformationRule);
    var sorted = Utils.sortByPriority(original);

    Assertions.assertEquals(2, sorted.size());
    Assertions.assertEquals(sorted.get(0).getRulePriority(), 100);
    Assertions.assertEquals(sorted.get(1).getRulePriority(), 210);
  }
}
