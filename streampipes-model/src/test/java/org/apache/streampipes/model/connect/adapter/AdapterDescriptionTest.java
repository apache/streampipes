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

package org.apache.streampipes.model.connect.adapter;

import org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AdapterDescriptionTest {

  AdapterDescription adapterDescription;

  @Before
  public void init() {
    adapterDescription = new AdapterDescription() {
    };

    List rules = new ArrayList<>();
    rules.add(new CreateNestedRuleDescription());
    rules.add(new CreateNestedRuleDescription());
    rules.add(new DeleteRuleDescription());
    rules.add(new UnitTransformRuleDescription());
    rules.add(new CreateNestedRuleDescription());
    rules.add(new CreateNestedRuleDescription());
    rules.add(new UnitTransformRuleDescription());
    rules.add(new MoveRuleDescription());
    rules.add(new CreateNestedRuleDescription());

    adapterDescription.setRules(rules);
  }

  @Test
  public void getValueRules() {
    assertEquals(2, adapterDescription.getValueRules().size());
  }

  @Test
  public void getStreamRules() {
    assertEquals(0, adapterDescription.getStreamRules().size());
  }

  @Test
  public void getSchemaRules() {
    assertEquals(7, adapterDescription.getSchemaRules().size());
  }
}