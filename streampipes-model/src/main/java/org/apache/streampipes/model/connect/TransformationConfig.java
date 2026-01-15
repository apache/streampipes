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

package org.apache.streampipes.model.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformationConfig {
  private boolean scriptActive;
  private String language;
  private String script;
  private List<Map<String, Object>> inputs;
  private List<Map<String, Object>> outputs;

  private ReduceEventRateRule reduceEventRateRule;
  private RemoveDuplicateRule removeDuplicateRule;

  public TransformationConfig() {
    this.inputs = new ArrayList<>();
    this.outputs = new ArrayList<>();
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public List<Map<String, Object>> getInputs() {
    return inputs;
  }

  public void setInputs(List<Map<String, Object>> inputs) {
    this.inputs = inputs;
  }

  public List<Map<String, Object>> getOutputs() {
    return outputs;
  }

  public void setOutputs(List<Map<String, Object>> outputs) {
    this.outputs = outputs;
  }

  public ReduceEventRateRule getReduceEventRateRule() {
    return reduceEventRateRule;
  }

  public void setReduceEventRateRule(ReduceEventRateRule reduceEventRateRule) {
    this.reduceEventRateRule = reduceEventRateRule;
  }

  public RemoveDuplicateRule getRemoveDuplicateRule() {
    return removeDuplicateRule;
  }

  public void setRemoveDuplicateRule(RemoveDuplicateRule removeDuplicateRule) {
    this.removeDuplicateRule = removeDuplicateRule;
  }

  public boolean isScriptActive() {
    return scriptActive;
  }

  public void setScriptActive(boolean scriptActive) {
    this.scriptActive = scriptActive;
  }
}
