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

export class CustomOutputController {

    outputStrategy: any;
    selectedElement: any;
    collectedPropertiesFirstStream: any;
    collectedPropertiesSecondStream: any;
    customizeForm: any;

    PropertySelectorService: any;

    constructor(PropertySelectorService) {
        this.PropertySelectorService = PropertySelectorService;
    }

    $onInit() {
        this.collectedPropertiesFirstStream = this.PropertySelectorService
            .makeProperties(this.getProperties(0), this.outputStrategy.properties.availablePropertyKeys, this.PropertySelectorService.firstStreamPrefix);
        this.collectedPropertiesSecondStream = this.PropertySelectorService
            .makeProperties(this.getProperties(1), this.outputStrategy.properties.availablePropertyKeys, this.PropertySelectorService.secondStreamPrefix);
    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined ? [] : this.selectedElement.inputStreams[streamIndex].eventSchema.eventProperties;
    }

    selectAll(collectedProperties) {
        collectedProperties.forEach(ep => this.outputStrategy.properties.selectedPropertyKeys.push(ep.properties.runtimeId));
        // This is needed to trigger update of scope
        this.outputStrategy.properties.selectedPropertyKeys = this.outputStrategy.properties.selectedPropertyKeys.filter(el => {return true;});
    }

    deselectAll(collectedProperties) {
        collectedProperties.forEach(ep => this.outputStrategy.properties.selectedPropertyKeys =
            this.outputStrategy.properties.selectedPropertyKeys.filter(item => item !== ep.properties.runtimeId));

    }
}

CustomOutputController.$inject = ['PropertySelectorService']