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
    private String fromUnitRessourceURL;

    @RdfProperty(StreamPipes.TO_UNIT)
    private String toUnitRessourceURL;

    public UnitTransformRuleDescription() {
        super();
    }

    public UnitTransformRuleDescription(String eventPropertyId, String fromUnitRessourceURL, String toUnit) {
        super();
        this.eventPropertyId = eventPropertyId;
        this.fromUnitRessourceURL = fromUnitRessourceURL;
        this.toUnitRessourceURL = toUnit;
    }

    public UnitTransformRuleDescription(UnitTransformRuleDescription other) {
        super(other);
        this.eventPropertyId = other.getEventPropertyId();
        this.fromUnitRessourceURL = other.getFromUnitRessourceURL();
        this.toUnitRessourceURL = other.getToUnitRessourceURL();
    }

    public String getEventPropertyId() {
        return eventPropertyId;
    }

    public void setEventPropertyId(String eventPropertyId) {
        this.eventPropertyId = eventPropertyId;
    }

    public String getFromUnitRessourceURL() {
        return fromUnitRessourceURL;
    }

    public void setFromUnitRessourceURL(String fromUnitRessourceURL) {
        this.fromUnitRessourceURL = fromUnitRessourceURL;
    }

    public String getToUnitRessourceURL() {
        return toUnitRessourceURL;
    }

    public void setToUnitRessourceURL(String toUnitRessourceURL) {
        this.toUnitRessourceURL = toUnitRessourceURL;
    }
}
