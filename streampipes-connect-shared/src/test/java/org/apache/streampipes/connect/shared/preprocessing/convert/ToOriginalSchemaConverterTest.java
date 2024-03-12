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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.getUnit;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeDeleteTransformationRule;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeMoveTransformationRule;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeNestedProperties;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeSimpleProperties;
import static org.apache.streampipes.connect.shared.preprocessing.convert.Helpers.makeUnitTransformationRule;

public class ToOriginalSchemaConverterTest {

  @Test
  public void testSimpleUnitConversion() {
    List<EventProperty> properties = makeSimpleProperties(true);

    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeUnitTransformationRule("stringProp"));

    var resultProperties = executeAndReturnResult(properties, rules);

    Assertions.assertEquals(3, resultProperties.size());
    Assertions.assertEquals("originalUnit", getUnit(resultProperties.get(0)));
  }

  @Test
  public void testNestedUnitConversion() {
    List<EventProperty> properties = makeNestedProperties();

    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeUnitTransformationRule("nested.stringProp"));

    var resultProperties = executeAndReturnResult(properties, rules);
    var nestedResultProperty = ((EventPropertyNested) resultProperties.get(1)).getEventProperties().get(0);

    Assertions.assertEquals(2, resultProperties.size());
    Assertions.assertEquals("originalUnit", getUnit(nestedResultProperty));
  }

  @Test
  public void testSimpleMoveConversion() {
    List<EventProperty> properties = makeNestedProperties();
    var nestedProperty = ((EventPropertyNested) properties.get(1));
    nestedProperty.getEventProperties().add(EpProperties.stringEp(Labels.empty(), "epToBeMoved", ""));

    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeMoveTransformationRule("epToBeMoved", "nested"));

    var result = executeAndReturnResult(properties, rules);
    Assertions.assertEquals(3, result.size());
    Assertions.assertEquals("timestamp",
                            result.get(0).getRuntimeName());
    Assertions.assertEquals(2,
                            ((EventPropertyNested) result.get(1)).getEventProperties().size());
  }

  @Test
  public void testDeleteRule() {
    List<EventProperty> properties = makeSimpleProperties(true);
    var rules = new ArrayList<TransformationRuleDescription>();

    rules.add(makeDeleteTransformationRule("epToBeRestored"));
    var result = executeAndReturnResult(properties, rules);
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("epToBeRestored",
                            result.get(3).getRuntimeName());
  }

  private List<EventProperty> executeAndReturnResult(List<EventProperty> properties,
                                                     List<TransformationRuleDescription> rules) {
    var result = new SchemaConverter().toOriginalSchema(new EventSchema(properties), rules);
    return result.getEventProperties();
  }
}
