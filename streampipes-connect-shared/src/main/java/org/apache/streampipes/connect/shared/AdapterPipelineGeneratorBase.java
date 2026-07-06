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

import org.apache.streampipes.connect.shared.preprocessing.elements.AdapterTransformationPipelineElement;
import org.apache.streampipes.connect.shared.preprocessing.transform.stream.EventRateTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.stream.RemoveDuplicatesTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.DatatypeTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.UnitTransformationRule;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.extensions.api.connect.TransformationRule;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterPipelineGeneratorBase {

  public List<IAdapterPipelineElement> makeAdapterPipelineElements(
      boolean includeStateful,
      AdapterDescription adapterDescription
  ) {
    var elements = new ArrayList<IAdapterPipelineElement>();

    List<TransformationRule> transformationRules = new ArrayList<>();
    if (includeStateful) {
      transformationRules.addAll(getReduceEventTransformationRule(adapterDescription));
      transformationRules.addAll(getRemoveDuplicateRule(adapterDescription));
    }
    transformationRules.addAll(getTypeConvertionRules(adapterDescription));
    transformationRules.addAll(getUnitConvertionRules(adapterDescription));
    elements.add(new AdapterTransformationPipelineElement(transformationRules));


    return elements;
  }

  private List<TransformationRule> getTypeConvertionRules(AdapterDescription adapterDescription) {
    return adapterDescription.getEventSchema()
                             .getEventProperties()
                             .stream()
                             .filter(ep -> ep.getAdditionalMetadata()
                                             .containsKey("originType"))
                             .map(ep -> new DatatypeTransformationRule(
                                 adapterDescription.getName(),
                                 ep.getRuntimeName(),
                                 ((EventPropertyPrimitive) ep).getRuntimeType()
                             ))
                             .collect(Collectors.toList());
  }



  private List<TransformationRule> getUnitConvertionRules(AdapterDescription adapterDescription) {
    return adapterDescription.getEventSchema()
                             .getEventProperties()
                             .stream()
                             .filter(ep -> (
                                 ep.getAdditionalMetadata()
                                   .containsKey("fromMeasurementUnit") && ep.getAdditionalMetadata()
                                                                            .containsKey("toMeasurementUnit")
                             ))
                             .map(ep -> {
                               String toUnit = ep.getAdditionalMetadata()
                                                 .get("toMeasurementUnit")
                                                 .toString();
                               String fromUnit = ep.getAdditionalMetadata()
                                                   .get("fromMeasurementUnit")
                                                   .toString();

                               var rule =
                                   new UnitTransformationRule(
                                       List.of(ep.getRuntimeName()),
                                       fromUnit,
                                       toUnit
                                   );
                               return rule;
                             })
                             .collect(Collectors.toList());
  }

  private List<TransformationRule> getReduceEventTransformationRule(AdapterDescription adapterDescription) {

    var result = new ArrayList<TransformationRule>();

    var reduceEventRate = adapterDescription.getTransformationConfig()
                                            .getReduceEventRateRule();

    if (reduceEventRate != null) {
      var rule = new EventRateTransformationRule(
          reduceEventRate.aggregationTimeWindow(),
          reduceEventRate.aggregationType()
      );
      result.add(rule);
    }

    return result;
  }

  private List<TransformationRule> getRemoveDuplicateRule(AdapterDescription adapterDescription) {

    var result = new ArrayList<TransformationRule>();
    var removeDuplicateRule = adapterDescription.getTransformationConfig()
                                                .getRemoveDuplicateRule();

    if (removeDuplicateRule != null) {
      var rule = new RemoveDuplicatesTransformationRule(
          removeDuplicateRule.filterTimeWindow()
      );
      result.add(rule);
    }

    return result;
  }

}
