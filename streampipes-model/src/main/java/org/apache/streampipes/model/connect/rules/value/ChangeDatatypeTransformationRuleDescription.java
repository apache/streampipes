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

public class ChangeDatatypeTransformationRuleDescription extends ValueTransformationRuleDescription {

  private String runtimeKey;
  private String originalDatatypeXsd;
  private String targetDatatypeXsd;

  public ChangeDatatypeTransformationRuleDescription() {
  }

  public ChangeDatatypeTransformationRuleDescription(ChangeDatatypeTransformationRuleDescription other) {
    super(other);
    this.runtimeKey = other.getRuntimeKey();
    this.originalDatatypeXsd = other.getOriginalDatatypeXsd();
    this.targetDatatypeXsd = other.getTargetDatatypeXsd();
  }

  public String getRuntimeKey() {
    return runtimeKey;
  }

  public void setRuntimeKey(String runtimeKey) {
    this.runtimeKey = runtimeKey;
  }

  public String getOriginalDatatypeXsd() {
    return originalDatatypeXsd;
  }

  public void setOriginalDatatypeXsd(String originalDatatypeXsd) {
    this.originalDatatypeXsd = originalDatatypeXsd;
  }

  public String getTargetDatatypeXsd() {
    return targetDatatypeXsd;
  }

  public void setTargetDatatypeXsd(String targetDatatypeXsd) {
    this.targetDatatypeXsd = targetDatatypeXsd;
  }

  @Override
  public void accept(ITransformationRuleVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int getRulePriority() {
    return TransformationRulePriority.CHANGE_DATATYPE.getCode();
  }
}
