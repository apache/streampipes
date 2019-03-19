/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic.pipeline.elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipelineElement;
import org.streampipes.connect.adapter.generic.pipeline.Util;
import org.streampipes.connect.adapter.generic.transform.value.*;
import org.streampipes.model.connect.rules.TransformationRuleDescription;
import org.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.streampipes.model.connect.rules.value.ValueTransformationRuleDescription;
import org.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformValueAdapterPipelineElement implements AdapterPipelineElement {

    private ValueEventTransformer eventTransformer;
    private Logger logger = LoggerFactory.getLogger(TransformValueAdapterPipelineElement.class);

    public TransformValueAdapterPipelineElement(List<ValueTransformationRuleDescription> transformationRuleDescriptions) {
        List<ValueTransformationRule> rules = new ArrayList<>();

        // transforms description to actual rules
        for (TransformationRuleDescription ruleDescription : transformationRuleDescriptions) {
            if (ruleDescription instanceof UnitTransformRuleDescription) {
                UnitTransformRuleDescription tmp = (UnitTransformRuleDescription) ruleDescription;
                rules.add(new UnitTransformationRule(Util.toKeyArray(tmp.getRuntimeKey()),
                        tmp.getFromUnitRessourceURL(), tmp.getToUnitRessourceURL()));
            } if(ruleDescription instanceof TimestampTranfsformationRuleDescription) {
                TimestampTranfsformationRuleDescription tmp = (TimestampTranfsformationRuleDescription) ruleDescription;
                TimestampTranformationRuleMode mode = null;
                switch (tmp.getMode()) {
                    case "formatString": mode = TimestampTranformationRuleMode.FORMAT_STRING;
                        break;
                    case "timeUnit": mode = TimestampTranformationRuleMode.TIME_UNIT;
                }
                rules.add(new TimestampTranformationRule(Util.toKeyArray(tmp.getRuntimeKey()), mode,
                        tmp.getFormatString(), tmp.getMultiplier()));
            }

            else {
                logger.error("Could not find the class for the rule description. This should never happen. Talk to admins to extend the rule implementations to get rid of this error!");
            }
        }

        eventTransformer = new ValueEventTransformer(rules);
    }

    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        return eventTransformer.transform(event);
    }
}
