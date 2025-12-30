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
import org.apache.streampipes.connect.shared.preprocessing.elements.ScriptTransformationPipelineElement;
import org.apache.streampipes.connect.shared.preprocessing.generator.StatefulTransformationRuleGeneratorVisitor;
import org.apache.streampipes.connect.shared.preprocessing.generator.StatelessTransformationRuleGeneratorVisitor;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterPipelineGeneratorBase {

  public List<IAdapterPipelineElement> makeAdapterPipelineElements(
      List<TransformationRuleDescription> rules,
      boolean includeStateful,
      AdapterDescription adapterDescription,
      boolean includeScript
  ) {
    // TODO clean up


    rules.addAll(getTypeConvertionRules(adapterDescription));

    rules.addAll(getUnitConvertionRules(adapterDescription));

    var elements = new ArrayList<IAdapterPipelineElement>();

    if (includeScript) {
      elements.add(new ScriptTransformationPipelineElement(
          adapterDescription.getTransformationConfig()
                            .getLanguage(),
          adapterDescription.getTransformationConfig()
                            .getScript()
      ));

    }


    elements.add(new AdapterTransformationPipelineElement(
                     rules,
                     new StatelessTransformationRuleGeneratorVisitor()
                 )
    );
    if (includeStateful) {
      elements.add(new AdapterTransformationPipelineElement(
                       rules,
                       new StatefulTransformationRuleGeneratorVisitor()
                   )
      );
    }
    return elements;
  }

  private List<TransformationRuleDescription> getTypeConvertionRules(AdapterDescription adapterDescription) {
    return adapterDescription.getEventSchema()
                             .getEventProperties()
                             .stream()
                             .filter(ep -> ep.getAdditionalMetadata()
                                             .containsKey("originType"))
                             .map(ep -> new ChangeDatatypeTransformationRuleDescription(
                                 ep.getRuntimeName(),
                                 ((EventPropertyPrimitive) ep).getRuntimeType()
                             ))
                             .collect(Collectors.toList());
  }

  private List<TransformationRuleDescription> getUnitConvertionRules(AdapterDescription adapterDescription) {
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
                                   new UnitTransformRuleDescription(
                                       ep.getRuntimeName(),
                                       fromUnit,
                                       toUnit
                                   );
                               return rule;
                             })
                             .collect(Collectors.toList());
  }

}
