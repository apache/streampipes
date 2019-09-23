/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

export class PropertySelectorService {

    firstStreamPrefix: string = "s0";
    secondStreamPrefix: string = "s1";
    propertyDelimiter: string = "::";

    constructor() {

    }

    makeProperties(eventProperties, availablePropertyKeys, currentPointer) {
        let outputProperties = [];

        eventProperties.forEach(ep => {
            availablePropertyKeys.forEach(apk => {
                if (this.isInSelection(ep, apk, currentPointer)) {
                    ep.properties.runtimeId = this.makeSelector(currentPointer, ep.properties.runtimeName);
                    if (this.isNested(ep)) {
                        ep.properties.eventProperties = this.makeProperties(ep.properties.eventProperties, availablePropertyKeys, this.makeSelector(currentPointer, ep.properties.runtimeName));
                    }
                    outputProperties.push(ep);
                }
            });
        });
        return outputProperties;
    }

    makeFlatProperties(eventProperties, availablePropertyKeys) {
        let outputProperties = [];

        availablePropertyKeys.forEach(apk => {
            let keyArray = apk.split("::");
            keyArray.shift();
            outputProperties.push(this.makeProperty(eventProperties, keyArray, apk));
        });
        return outputProperties;
    }

    makeProperty(eventProperties, propertySelector, originalSelector) {
        let outputProperty;
        eventProperties.forEach(ep => {
            if (ep.properties.runtimeName === propertySelector[0]) {
                if (this.isNested(ep)) {
                    propertySelector.shift();
                    outputProperty = this.makeProperty(ep.properties.eventProperties, propertySelector, originalSelector);
                } else {
                    ep.properties.runtimeId = originalSelector;
                    outputProperty = ep;
                    outputProperty.properties.niceLabel = this.makeNiceLabel(originalSelector);
                }
            }
        });
        return outputProperty;
    }

    makeNiceLabel(originalSelector) {
        return originalSelector.split("::").join(" \u{21D2} ");
    }

    isNested(ep) {
        return ep.type === "org.streampipes.model.schema.EventPropertyNested";
    }

    isInSelection(inputProperty, propertySelector, currentPropertyPointer) {
        return (currentPropertyPointer
            + this.propertyDelimiter
            + inputProperty.properties.runtimeName) === propertySelector;

    }

    makeSelector(prefix, current) {
        return prefix + this.propertyDelimiter + current;
    }
}