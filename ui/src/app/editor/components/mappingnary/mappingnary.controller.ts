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

export class MappingNaryController {

    staticProperty: any;
    selectedElement: any;
    availableProperties: any;
    PropertySelectorService: any;

    constructor(PropertySelectorService) {
        this.PropertySelectorService = PropertySelectorService;
    }

    $onInit() {
        this.availableProperties = this.PropertySelectorService.makeFlatProperties(this.getProperties(this.findIndex()), this.staticProperty.properties.mapsFromOptions);
    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined ? [] : this.selectedElement.inputStreams[streamIndex].eventSchema.eventProperties;
    }

    findIndex() {
        let prefix = this.staticProperty.properties.mapsFromOptions[0].split("::");
        prefix = prefix[0].replace("s", "");
        return prefix;
    }

    toggle(property, staticProperty) {
        if (this.exists(property, staticProperty)) {
            this.remove(property, staticProperty);
        } else {
            this.add(property, staticProperty);
        }
    }

    exists(property, staticProperty) {
        if (!staticProperty.properties.selectedProperties) return false;
        return staticProperty.properties.selectedProperties.indexOf(property.properties.runtimeId) > -1;
    }

    add(property, staticProperty) {
        if (!staticProperty.properties.selectedProperties) {
            staticProperty.properties.selectedProperties = [];
        }
        staticProperty.properties.selectedProperties.push(property.properties.runtimeId);
    }

    remove(property, staticProperty) {
        var index = staticProperty.properties.selectedProperties.indexOf(property.properties.runtimeId);
        staticProperty.properties.selectedProperties.splice(index, 1);
    }

    selectAll() {
        this.staticProperty.properties.selectedProperties = this.staticProperty.properties.mapsFromOptions;
    }

    deselectAll() {
        this.staticProperty.properties.selectedProperties = [];
    }
}

MappingNaryController.$inject=['PropertySelectorService']