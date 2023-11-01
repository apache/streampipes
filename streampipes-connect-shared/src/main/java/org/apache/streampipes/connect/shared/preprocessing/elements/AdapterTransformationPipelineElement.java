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

package org.apache.streampipes.connect.shared.preprocessing.elements;

import org.apache.streampipes.connect.shared.preprocessing.generator.TransformationRuleGeneratorVisitor;
import org.apache.streampipes.connect.shared.preprocessing.transform.TransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.utils.Utils;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;

import java.util.List;
import java.util.Map;

public class AdapterTransformationPipelineElement implements IAdapterPipelineElement {

  private final List<TransformationRule> transformationRules;

  public AdapterTransformationPipelineElement(List<TransformationRuleDescription> transformationRules,
                                              TransformationRuleGeneratorVisitor visitor) {
    var descriptions = Utils.sortByPriority(transformationRules);

    descriptions.forEach(d -> d.accept(visitor));
    this.transformationRules = visitor.getTransformationRules();
  }

  @Override
  public Map<String, Object> process(Map<String, Object> event) {
    for (TransformationRule rule : transformationRules) {
      event = rule.apply(event);
    }
    return event;
  }
}
