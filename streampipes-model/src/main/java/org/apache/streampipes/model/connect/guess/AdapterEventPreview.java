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

package org.apache.streampipes.model.connect.guess;

import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.List;

@TsModel
public class AdapterEventPreview {

  private List<TransformationRuleDescription> rules;

  private String inputData;

  public List<TransformationRuleDescription> getRules() {
    return rules;
  }

  public void setRules(List<TransformationRuleDescription> rules) {
    this.rules = rules;
  }

  public String getInputData() {
    return inputData;
  }

  public void setInputData(String inputData) {
    this.inputData = inputData;
  }
}
