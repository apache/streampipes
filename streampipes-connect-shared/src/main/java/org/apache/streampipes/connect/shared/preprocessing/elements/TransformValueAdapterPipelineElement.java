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

import org.apache.streampipes.connect.shared.preprocessing.Util;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.CorrectionValueTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.DatatypeTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.TimestampTranformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.TimestampTranformationRuleMode;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.UnitTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.ValueEventTransformer;
import org.apache.streampipes.connect.shared.preprocessing.transform.value.ValueTransformationRule;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformValueAdapterPipelineElement implements IAdapterPipelineElement {

  private final ValueEventTransformer eventTransformer;
  private static final Logger logger = LoggerFactory.getLogger(TransformValueAdapterPipelineElement.class);

  public TransformValueAdapterPipelineElement(
      List<? extends TransformationRuleDescription> transformationRuleDescriptions) {
    List<ValueTransformationRule> rules = new ArrayList<>();

    // transforms description to actual rules
    for (TransformationRuleDescription ruleDescription : transformationRuleDescriptions) {
      if (ruleDescription instanceof UnitTransformRuleDescription) {
        var tmp = (UnitTransformRuleDescription) ruleDescription;
        rules.add(new UnitTransformationRule(Util.toKeyArray(tmp.getRuntimeKey()),
            tmp.getFromUnitRessourceURL(), tmp.getToUnitRessourceURL()));
      } else if (ruleDescription instanceof TimestampTranfsformationRuleDescription) {
        var tmp = (TimestampTranfsformationRuleDescription) ruleDescription;
        TimestampTranformationRuleMode mode = null;
        switch (tmp.getMode()) {
          case "formatString":
            mode = TimestampTranformationRuleMode.FORMAT_STRING;
            break;
          case "timeUnit":
            mode = TimestampTranformationRuleMode.TIME_UNIT;
        }
        rules.add(new TimestampTranformationRule(Util.toKeyArray(tmp.getRuntimeKey()), mode,
            tmp.getFormatString(), tmp.getMultiplier()));
      } else if (ruleDescription instanceof CorrectionValueTransformationRuleDescription) {
        var tmp = (CorrectionValueTransformationRuleDescription) ruleDescription;
        rules.add(new CorrectionValueTransformationRule(Util.toKeyArray(tmp.getRuntimeKey()), tmp.getCorrectionValue(),
            tmp.getOperator()));
      } else if (ruleDescription instanceof ChangeDatatypeTransformationRuleDescription) {
        var tmp = (ChangeDatatypeTransformationRuleDescription) ruleDescription;
        rules.add(new DatatypeTransformationRule(tmp.getRuntimeKey(), tmp.getOriginalDatatypeXsd(),
            tmp.getTargetDatatypeXsd()));
      } else {
        logger.error(
            "Could not find the class for the rule description. This should never happen. "
                + "Talk to admins to extend the rule implementations to get rid of this error!");
      }
    }

    eventTransformer = new ValueEventTransformer(rules);
  }

  @Override
  public Map<String, Object> process(Map<String, Object> event) {
    return eventTransformer.transform(event);
  }

  public ValueEventTransformer getEventTransformer() {
    return eventTransformer;
  }
}
