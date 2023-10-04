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

import org.apache.streampipes.connect.shared.preprocessing.transform.schema.AddValueTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.CreateNestedTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.DeleteTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.MoveTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.RenameTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.AddTimestampTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.CorrectionValueTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.DatatypeTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.TimestampTranformationRuleMode;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.TimestampTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.UnitTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.utils.Utils;
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

public class StatelessTransformationRuleGeneratorVisitor extends TransformationRuleGeneratorVisitor {

  @Override
  public void visit(CreateNestedRuleDescription ruleDesc) {
    rules.add(new CreateNestedTransformationRule(
        Utils.toKeyArray(ruleDesc.getRuntimeKey())));
  }

  @Override
  public void visit(DeleteRuleDescription ruleDesc) {
    rules.add(new DeleteTransformationRule(
        Utils.toKeyArray(ruleDesc.getRuntimeKey())));
  }

  @Override
  public void visit(MoveRuleDescription ruleDesc) {
    rules.add(new MoveTransformationRule(
        Utils.toKeyArray(ruleDesc.getOldRuntimeKey()),
        Utils.toKeyArray(ruleDesc.getNewRuntimeKey())));
  }

  @Override
  public void visit(RenameRuleDescription ruleDesc) {
    rules.add(new RenameTransformationRule(
        Utils.toKeyArray(ruleDesc.getOldRuntimeKey()),
        Utils.getLastKey(ruleDesc.getNewRuntimeKey())));
  }

  @Override
  public void visit(EventRateTransformationRuleDescription ruleDesc) {
    // Do nothing
  }

  @Override
  public void visit(RemoveDuplicatesTransformationRuleDescription ruleDesc) {
    // Do nothing
  }

  @Override
  public void visit(AddTimestampRuleDescription ruleDesc) {
    rules.add(new AddTimestampTransformationRule(
        ruleDesc.getRuntimeKey()));
  }

  @Override
  public void visit(AddValueTransformationRuleDescription ruleDesc) {
    rules.add(new AddValueTransformationRule(
        ruleDesc.getRuntimeKey(),
        ruleDesc.getStaticValue()));
  }

  @Override
  public void visit(ChangeDatatypeTransformationRuleDescription ruleDesc) {
    rules.add(new DatatypeTransformationRule(
        ruleDesc.getRuntimeKey(),
        ruleDesc.getOriginalDatatypeXsd(),
        ruleDesc.getTargetDatatypeXsd()));
  }

  @Override
  public void visit(CorrectionValueTransformationRuleDescription ruleDesc) {
    rules.add(new CorrectionValueTransformationRule(
        Utils.toKeyArray(ruleDesc.getRuntimeKey()),
        ruleDesc.getCorrectionValue(),
        ruleDesc.getOperator()));
  }

  @Override
  public void visit(TimestampTranfsformationRuleDescription ruleDesc) {
    TimestampTranformationRuleMode mode;
    if (ruleDesc.getMode().equals(TimestampTranformationRuleMode.FORMAT_STRING.internalName())) {
      mode = TimestampTranformationRuleMode.FORMAT_STRING;
    } else {
      mode = TimestampTranformationRuleMode.FORMAT_STRING;
    }

    rules.add(new TimestampTransformationRule(Utils.toKeyArray(
        ruleDesc.getRuntimeKey()),
        mode,
        ruleDesc.getFormatString(),
        ruleDesc.getMultiplier()));
  }

  @Override
  public void visit(UnitTransformRuleDescription ruleDesc) {
    rules.add(new UnitTransformationRule(ruleDesc));
  }

}
