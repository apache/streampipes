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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AdapterDescriptionTest {

  AdapterDescription adapterDescription;

  @BeforeEach
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
    Assertions.assertEquals(2, adapterDescription.getValueRules().size());
  }

  @Test
  public void getStreamRules() {
    Assertions.assertEquals(0, adapterDescription.getStreamRules().size());
  }

  @Test
  public void getSchemaRules() {
    Assertions.assertEquals(7, adapterDescription.getSchemaRules().size());
  }
}