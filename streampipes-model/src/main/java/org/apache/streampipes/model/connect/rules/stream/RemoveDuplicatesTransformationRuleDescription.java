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

package org.apache.streampipes.model.connect.rules.stream;

import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.TransformationRulePriority;

public class RemoveDuplicatesTransformationRuleDescription extends StreamTransformationRuleDescription {

  private String filterTimeWindow;

  public RemoveDuplicatesTransformationRuleDescription() {
    super();
  }

  public RemoveDuplicatesTransformationRuleDescription(RemoveDuplicatesTransformationRuleDescription other) {
    super(other);
    this.filterTimeWindow = other.getFilterTimeWindow();
  }

  public String getFilterTimeWindow() {
    return filterTimeWindow;
  }

  public void setFilterTimeWindow(String filterTimeWindow) {
    this.filterTimeWindow = filterTimeWindow;
  }

  @Override
  public void accept(ITransformationRuleVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int getRulePriority() {
    return TransformationRulePriority.REMOVE_DUPLICATES.getCode();
  }
}
