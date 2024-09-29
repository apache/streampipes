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
package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.connect.shared.preprocessing.utils.Utils;
import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.ArrayList;
import java.util.List;

public class TransformationRuleUpdateVisitor implements ITransformationRuleVisitor {

  private final List<String> existingPropertyRuntimeNames;
  private final List<TransformationRuleDescription> validRules;
  private final List<TransformationRuleDescription> allRules;

  public TransformationRuleUpdateVisitor(List<EventProperty> existingProperties,
          List<TransformationRuleDescription> allRules) {
    this.existingPropertyRuntimeNames = existingProperties.stream().map(EventProperty::getRuntimeName).toList();
    this.allRules = allRules;
    this.validRules = new ArrayList<>();
  }

  @Override
  public void visit(CreateNestedRuleDescription rule) {
    validRules.add(rule);
  }

  @Override
  public void visit(DeleteRuleDescription rule) {
    if (containsKey(rule.getRuntimeKey())) {
      validRules.add(rule);
    }
  }

  @Override
  public void visit(MoveRuleDescription rule) {
    if (containsKey(rule.getOldRuntimeKey())) {
      validRules.add(rule);
    }
  }

  @Override
  public void visit(RenameRuleDescription rule) {
    if (containsKey(rule.getOldRuntimeKey())) {
      validRules.add(rule);
    }
  }

  @Override
  public void visit(EventRateTransformationRuleDescription rule) {
    // Do nothing
  }

  @Override
  public void visit(RemoveDuplicatesTransformationRuleDescription rule) {
    // Do nothing
  }

  @Override
  public void visit(AddTimestampRuleDescription rule) {
    validRules.add(rule);
  }

  @Override
  public void visit(AddValueTransformationRuleDescription rule) {
    validRules.add(rule);
  }

  @Override
  public void visit(ChangeDatatypeTransformationRuleDescription rule) {
    if (containsKey(rule.getRuntimeKey())) {
      validRules.add(rule);
    }
  }

  @Override
  public void visit(CorrectionValueTransformationRuleDescription rule) {
    if (containsKey(rule.getRuntimeKey())) {
      validRules.add(rule);
    }
  }

  @Override
  public void visit(TimestampTranfsformationRuleDescription rule) {
    if (containsKey(rule.getRuntimeKey())) {
      validRules.add(rule);
    }
  }

  @Override
  public void visit(UnitTransformRuleDescription rule) {
    if (containsKey(rule.getRuntimeKey())) {
      validRules.add(rule);
    }
  }

  private boolean containsKey(String fullRuntimeKey) {
    var runtimeKeys = Utils.toKeyArray(fullRuntimeKey);
    if (!runtimeKeys.isEmpty()) {
      return this.existingPropertyRuntimeNames.contains(runtimeKeys.get(0)) || inRenameRule(runtimeKeys.get(0));
    } else {
      return false;
    }
  }

  private boolean inRenameRule(String runtimeKey) {
    return this.allRules.stream().filter(rule -> rule instanceof RenameRuleDescription)
            .anyMatch(rule -> ((RenameRuleDescription) rule).getNewRuntimeKey().equals(runtimeKey));
  }

  public List<TransformationRuleDescription> getValidRules() {
    return validRules;
  }

}
