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

package org.streampipes.model.connect.rules.value;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS})
@RdfsClass(StreamPipes.UNIT_TRANSFORM_RULE_DESCRIPTION)
@Entity
public class UnitTransformRuleDescription extends ValueTransformationRuleDescription {

    @RdfProperty(StreamPipes.EVENT_PROPERTY_ID)
    private String eventPropertyId;

    @RdfProperty(StreamPipes.FROM_UNIT)
    private String fromUnit;

    @RdfProperty(StreamPipes.TO_UNIT)
    private String toUnit;

    public UnitTransformRuleDescription() {
        super();
    }

    public UnitTransformRuleDescription(String eventPropertyId, String fromUnit, String toUnit) {
        super();
        this.eventPropertyId = eventPropertyId;
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
    }

    public UnitTransformRuleDescription(UnitTransformRuleDescription other) {
        super(other);
        this.eventPropertyId = other.getEventPropertyId();
        this.fromUnit = other.getFromUnit();
        this.toUnit = other.getToUnit();
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(String fromUnit) {
        this.fromUnit = fromUnit;
    }

    public String getToUnit() {
        return toUnit;
    }

    public void setToUnit(String toUnit) {
        this.toUnit = toUnit;
    }

    public String getEventPropertyId() {
        return eventPropertyId;
    }

    public void setEventPropertyId(String runtimeKey) {
        this.eventPropertyId = runtimeKey;
    }

}
