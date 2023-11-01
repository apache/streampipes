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

package org.apache.streampipes.connect.shared.preprocessing.generator;

import org.apache.streampipes.connect.shared.preprocessing.transform.stream.DuplicateFilterPipelineElement;
import org.apache.streampipes.connect.shared.preprocessing.transform.stream.EventRateTransformationRule;
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

public class StatefulTransformationRuleGeneratorVisitor extends TransformationRuleGeneratorVisitor {

  @Override
  public void visit(CreateNestedRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(DeleteRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(MoveRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(RenameRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(EventRateTransformationRuleDescription ruleDesc) {
    rules.add(
        new EventRateTransformationRule(ruleDesc.getAggregationTimeWindow(), ruleDesc.getAggregationType()));
  }

  @Override
  public void visit(RemoveDuplicatesTransformationRuleDescription ruleDesc) {
    this.rules.add(
        new DuplicateFilterPipelineElement(ruleDesc.getFilterTimeWindow()));
  }

  @Override
  public void visit(AddTimestampRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(AddValueTransformationRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(ChangeDatatypeTransformationRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(CorrectionValueTransformationRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(TimestampTranfsformationRuleDescription rule) {
    // skip (not a stateful transformation)
  }

  @Override
  public void visit(UnitTransformRuleDescription rule) {
    // skip (not a stateful transformation)
  }
}
