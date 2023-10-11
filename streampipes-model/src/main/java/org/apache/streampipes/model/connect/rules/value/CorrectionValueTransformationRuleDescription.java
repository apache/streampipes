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

package org.apache.streampipes.model.connect.rules.value;

import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.TransformationRulePriority;

public class CorrectionValueTransformationRuleDescription extends ValueTransformationRuleDescription {

  private String runtimeKey;

  private double correctionValue;

  private String operator;

  public CorrectionValueTransformationRuleDescription() {
    super();
  }

  public CorrectionValueTransformationRuleDescription(String runtimeKey, double correctionValue, String operator) {
    this.runtimeKey = runtimeKey;
    this.correctionValue = correctionValue;
    this.operator = operator;
  }

  public CorrectionValueTransformationRuleDescription(CorrectionValueTransformationRuleDescription other) {
    super(other);
    this.runtimeKey = other.getRuntimeKey();
    this.correctionValue = other.getCorrectionValue();
    this.operator = other.getOperator();
  }

  public String getRuntimeKey() {
    return runtimeKey;
  }

  public void setRuntimeKey(String runtimeKey) {
    this.runtimeKey = runtimeKey;
  }

  public double getCorrectionValue() {
    return correctionValue;
  }

  public void setCorrectionValue(double correctionValue) {
    this.correctionValue = correctionValue;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  @Override
  public void accept(ITransformationRuleVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int getRulePriority() {
    return TransformationRulePriority.CORRECTION_VALUE.getCode();
  }
}
