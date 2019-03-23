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

package org.streampipes.connect.adapter.generic.transform.value;

import org.streampipes.connect.adapter.generic.transform.TransformationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValueEventTransformer implements ValueTransformationRule {

    private List<UnitTransformationRule> unitTransformationRules;
    private List<TimestampTranformationRule> timestampTranformationRules;

    public ValueEventTransformer(List<ValueTransformationRule> rules) {
        this.unitTransformationRules = new ArrayList<>();
        timestampTranformationRules = new ArrayList<>();

        for (TransformationRule rule : rules) {
            if (rule instanceof UnitTransformationRule) {
                this.unitTransformationRules.add((UnitTransformationRule) rule);
            } else if (rule instanceof TimestampTranformationRule) {
                this.timestampTranformationRules.add((TimestampTranformationRule) rule);
            }
        }
    }

/*
    public ValueEventTransformer(List<UnitTransformationRule> unitTransformationRule) {
        this.unitTransformationRules = new ArrayList<>();
    }
*/

    @Override
    public Map<String, Object> transform(Map<String, Object> event) {

        for (UnitTransformationRule unitRule : unitTransformationRules) {
            event = unitRule.transform(event);
        }

        for (TimestampTranformationRule unitRule : timestampTranformationRules) {
            event = unitRule.transform(event);
        }


        return event;
    }


    public List<UnitTransformationRule> getUnitTransformationRules() {
        return unitTransformationRules;
    }

    public void setUnitTransformationRules(List<UnitTransformationRule> unitTransformationRules) {
        this.unitTransformationRules = unitTransformationRules;
    }

    public List<TimestampTranformationRule> getTimestampTranformationRules() {
        return timestampTranformationRules;
    }

    public void setTimestampTranformationRules(List<TimestampTranformationRule> timestampTranformationRules) {
        this.timestampTranformationRules = timestampTranformationRules;
    }
}
