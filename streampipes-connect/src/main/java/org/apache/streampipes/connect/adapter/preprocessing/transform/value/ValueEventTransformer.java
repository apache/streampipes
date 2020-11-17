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

package org.apache.streampipes.connect.adapter.preprocessing.transform.value;

import org.apache.streampipes.connect.adapter.preprocessing.transform.TransformationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValueEventTransformer implements ValueTransformationRule {

    private List<UnitTransformationRule> unitTransformationRules;
    private List<TimestampTranformationRule> timestampTransformationRules;
    private List<CorrectionValueTransformationRule> correctionValueTransformationRules;

    public ValueEventTransformer(List<ValueTransformationRule> rules) {
        this.unitTransformationRules = new ArrayList<>();
        this.timestampTransformationRules = new ArrayList<>();
        this.correctionValueTransformationRules = new ArrayList<>();

        for (TransformationRule rule : rules) {
            if (rule instanceof UnitTransformationRule) {
                this.unitTransformationRules.add((UnitTransformationRule) rule);
            } else if (rule instanceof TimestampTranformationRule) {
                this.timestampTransformationRules.add((TimestampTranformationRule) rule);
            } else if (rule instanceof CorrectionValueTransformationRule) {
                this.correctionValueTransformationRules.add((CorrectionValueTransformationRule) rule);
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

        for (UnitTransformationRule rule : unitTransformationRules) {
            event = rule.transform(event);
        }

        for (TimestampTranformationRule rule : timestampTransformationRules) {
            event = rule.transform(event);
        }

        for (CorrectionValueTransformationRule rule : correctionValueTransformationRules) {
            event = rule.transform(event);
        }


        return event;
    }


    public List<UnitTransformationRule> getUnitTransformationRules() {
        return unitTransformationRules;
    }

    public void setUnitTransformationRules(List<UnitTransformationRule> unitTransformationRules) {
        this.unitTransformationRules = unitTransformationRules;
    }

    public List<TimestampTranformationRule> getTimestampTransformationRules() {
        return timestampTransformationRules;
    }

    public void setTimestampTransformationRules(List<TimestampTranformationRule> timestampTransformationRules) {
        this.timestampTransformationRules = timestampTransformationRules;
    }

    public List<CorrectionValueTransformationRule> getCorrectionValueTransformationRules() {
        return correctionValueTransformationRules;
    }

    public void setCorrectionValueTransformationRules(List<CorrectionValueTransformationRule> correctionValueTransformationRules) {
        this.correctionValueTransformationRules = correctionValueTransformationRules;
    }
}
