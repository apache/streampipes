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

import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.ArrayList;
import java.util.List;

public class Helpers {

  public static String getUnit(EventProperty eventProperty) {
    return ((EventPropertyPrimitive) eventProperty).getMeasurementUnit().toString();
  }

  public static List<EventProperty> makeSimpleProperties(boolean addTimestamp) {
    List<EventProperty> properties = new ArrayList<>();
    properties.add(EpProperties.stringEp(Labels.empty(), "stringProp", ""));
    properties.add(EpProperties.integerEp(Labels.empty(), "intProp", ""));
    if (addTimestamp) {
      properties.add(EpProperties.timestampProperty("timestamp"));
    }
    return properties;
  }

  public static List<EventProperty> makeNestedProperties() {
    List<EventProperty> properties = new ArrayList<>();
    properties.add(EpProperties.timestampProperty("timestamp"));
    var nestedProperties = makeSimpleProperties(false);
    var nestedProperty = new EventPropertyNested();
    nestedProperty.setRuntimeName("nested");
    nestedProperty.setEventProperties(nestedProperties);
    properties.add(nestedProperty);
    return properties;
  }

  public static UnitTransformRuleDescription makeUnitTransformationRule(String runtimeKey) {
    var rule = new UnitTransformRuleDescription();
    rule.setRuntimeKey(runtimeKey);
    rule.setFromUnitRessourceURL("originalUnit");
    rule.setToUnitRessourceURL("targetUnit");

    return rule;
  }

  public static MoveRuleDescription makeMoveTransformationRule(String runtimeKey,
                                                         String newRuntimeKey) {
    var rule = new MoveRuleDescription();
    rule.setOldRuntimeKey(runtimeKey);
    rule.setNewRuntimeKey(newRuntimeKey);
    return rule;
  }

  public static RenameRuleDescription makeRenameTransformationRule(String runtimeKey,
                                                             String newRuntimeName) {
    var rule = new RenameRuleDescription();
    rule.setOldRuntimeKey(runtimeKey);
    rule.setNewRuntimeKey(newRuntimeName);

    return rule;
  }

  public static DeleteRuleDescription makeDeleteTransformationRule(String runtimeKey) {
    var rule = new DeleteRuleDescription();
    rule.setRuntimeKey(runtimeKey);
    return rule;
  }
}
