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

package org.apache.streampipes.connect.shared;

import org.apache.streampipes.connect.shared.preprocessing.elements.AddTimestampPipelineElement;
import org.apache.streampipes.connect.shared.preprocessing.elements.AddValuePipelineElement;
import org.apache.streampipes.connect.shared.preprocessing.elements.TransformSchemaAdapterPipelineElement;
import org.apache.streampipes.connect.shared.preprocessing.elements.TransformValueAdapterPipelineElement;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.SchemaTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ValueTransformationRuleDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterPipelineGeneratorBase {

  public List<IAdapterPipelineElement> makeAdapterPipelineElements(List<TransformationRuleDescription> rules) {
    List<IAdapterPipelineElement> pipelineElements = new ArrayList<>();

    // Must be before the schema transformations to ensure that user can move this event property
    var timestampTransformationRuleDescription = getTimestampRule(rules);
    if (timestampTransformationRuleDescription != null) {
      pipelineElements.add(new AddTimestampPipelineElement(
          timestampTransformationRuleDescription.getRuntimeKey()));
    }

    var valueTransformationRuleDescription = getAddValueRule(rules);
    if (valueTransformationRuleDescription != null) {
      pipelineElements.add(new AddValuePipelineElement(
          valueTransformationRuleDescription.getRuntimeKey(),
          valueTransformationRuleDescription.getStaticValue()));
    }

    // first transform schema before transforming vales
    // value rules should use unique keys for of new schema
    pipelineElements.add(new TransformSchemaAdapterPipelineElement(getSchemaRules(rules)));
    pipelineElements.add(new TransformValueAdapterPipelineElement(getValueRules(rules)));

    return pipelineElements;
  }

  protected RemoveDuplicatesTransformationRuleDescription getRemoveDuplicateRule(
      List<TransformationRuleDescription> rules) {
    return getRule(rules, RemoveDuplicatesTransformationRuleDescription.class);
  }

  protected EventRateTransformationRuleDescription getEventRateTransformationRule(
      List<TransformationRuleDescription> rules) {
    return getRule(rules, EventRateTransformationRuleDescription.class);
  }

  protected AddTimestampRuleDescription getTimestampRule(List<TransformationRuleDescription> rules) {
    return getRule(rules, AddTimestampRuleDescription.class);
  }

  protected AddValueTransformationRuleDescription getAddValueRule(List<TransformationRuleDescription> rules) {
    return getRule(rules, AddValueTransformationRuleDescription.class);
  }

  private <T extends TransformationRuleDescription> T getRule(List<TransformationRuleDescription> rules,
                                                              Class<T> type) {

    if (rules != null) {
      for (TransformationRuleDescription tr : rules) {
        if (type.isInstance(tr)) {
          return type.cast(tr);
        }
      }
    }

    return null;
  }

  private List<TransformationRuleDescription> getValueRules(List<TransformationRuleDescription> rules) {
    return rules
        .stream()
        .filter(r -> r instanceof ValueTransformationRuleDescription && !(r instanceof AddTimestampRuleDescription))
        .collect(Collectors.toList());
  }

  private List<TransformationRuleDescription> getSchemaRules(List<TransformationRuleDescription> rules) {
    return rules
        .stream()
        .filter(r -> r instanceof SchemaTransformationRuleDescription)
        .collect(Collectors.toList());
  }
}
