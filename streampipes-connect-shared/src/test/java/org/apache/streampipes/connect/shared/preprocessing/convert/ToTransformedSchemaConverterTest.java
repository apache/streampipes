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

package org.apache.streampipes.connect.shared.preprocessing.convert;

import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.getUnit;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeDeleteTransformationRule;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeMoveTransformationRule;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeNestedProperties;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeRenameTransformationRule;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeSimpleProperties;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeUnitTransformationRule;
import static org.junit.Assert.assertEquals;

public class ToTransformedSchemaConverterTest {

  @Test
  public void testSimpleUnitConversion() {
    List<EventProperty> properties = makeSimpleProperties(true);

    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeUnitTransformationRule("stringProp"));

    var resultProperties = executeAndReturnResult(properties, rules);

    assertEquals(3, resultProperties.size());
    assertEquals("targetUnit", getUnit(resultProperties.get(0)));
  }


  @Test
  public void testSimpleRenameConversion() {
    List<EventProperty> properties = makeSimpleProperties(true);
    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeRenameTransformationRule("stringProp", "newStringProp"));

    var result = executeAndReturnResult(properties, rules);
    assertEquals(3, result.size());
    assertEquals("newStringProp", result.get(0).getRuntimeName());
  }

  @Test
  public void testSimpleDeleteConversion() {
    List<EventProperty> properties = makeSimpleProperties(true);
    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeDeleteTransformationRule("stringProp"));

    var result = executeAndReturnResult(properties, rules);
    assertEquals(2, result.size());
    assertEquals("intProp", result.get(0).getRuntimeName());
  }

  @Test
  public void testSimpleMoveConversion() {
    List<EventProperty> properties = makeNestedProperties();
    properties.add(EpProperties.stringEp(Labels.empty(), "epToBeMoved", ""));

    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeMoveTransformationRule("epToBeMoved", "nested"));

    var result = executeAndReturnResult(properties, rules);
    assertEquals(2, result.size());
    assertEquals("timestamp", result.get(0).getRuntimeName());
    assertEquals(3, ((EventPropertyNested) result.get(1)).getEventProperties().size());
  }

  @Test
  public void testNestedUnitConversion() {
    List<EventProperty> properties = makeNestedProperties();

    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeUnitTransformationRule("nested.stringProp"));

    var resultProperties = executeAndReturnResult(properties, rules);
    var nestedResultProperty = ((EventPropertyNested) resultProperties.get(1)).getEventProperties().get(0);

    assertEquals(2, resultProperties.size());
    assertEquals("targetUnit", getUnit(nestedResultProperty));
  }

  private List<EventProperty> executeAndReturnResult(List<EventProperty> properties,
                                                     List<TransformationRuleDescription> rules) {
    var result = new SchemaConverter().toTransformedSchema(new EventSchema(properties), rules);
    return result.getEventProperties();
  }



}
