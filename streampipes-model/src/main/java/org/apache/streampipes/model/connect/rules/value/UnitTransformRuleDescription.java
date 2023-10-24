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

public class UnitTransformRuleDescription extends ValueTransformationRuleDescription {

  private String runtimeKey;

  private String fromUnitRessourceURL;

  private String toUnitRessourceURL;

  public UnitTransformRuleDescription() {
    super();
  }

  public UnitTransformRuleDescription(String runtimeKey, String fromUnitRessourceURL, String toUnit) {
    super();
    this.runtimeKey = runtimeKey;
    this.fromUnitRessourceURL = fromUnitRessourceURL;
    this.toUnitRessourceURL = toUnit;
  }

  public UnitTransformRuleDescription(UnitTransformRuleDescription other) {
    super(other);
    this.runtimeKey = other.getRuntimeKey();
    this.fromUnitRessourceURL = other.getFromUnitRessourceURL();
    this.toUnitRessourceURL = other.getToUnitRessourceURL();
  }

  public String getRuntimeKey() {
    return runtimeKey;
  }

  public void setRuntimeKey(String runtimeKey) {
    this.runtimeKey = runtimeKey;
  }

  public String getFromUnitRessourceURL() {
    return fromUnitRessourceURL;
  }

  public void setFromUnitRessourceURL(String fromUnitRessourceURL) {
    this.fromUnitRessourceURL = fromUnitRessourceURL;
  }

  public String getToUnitRessourceURL() {
    return toUnitRessourceURL;
  }

  public void setToUnitRessourceURL(String toUnitRessourceURL) {
    this.toUnitRessourceURL = toUnitRessourceURL;
  }

  @Override
  public void accept(ITransformationRuleVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int getRulePriority() {
    return TransformationRulePriority.CHANGE_UNIT.getCode();
  }
}
