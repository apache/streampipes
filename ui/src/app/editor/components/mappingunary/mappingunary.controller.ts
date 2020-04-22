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

import {PropertySelectorService} from "../../../services/property-selector.service";

export class MappingUnaryController {

    staticProperty: any;
    selectedElement: any;
    availableProperties: any;

    $scope: any;
    $rootScope: any;
    PropertySelectorService: PropertySelectorService;

    constructor($scope, $rootScope, PropertySelectorService, private $timeout) {
        this.$scope = $scope;
        this.$rootScope = $rootScope;
        this.PropertySelectorService = PropertySelectorService;
    }

    $onInit() {
        this.availableProperties = this.PropertySelectorService.makeFlatProperties(this.getProperties(this.findIndex()), this.staticProperty.properties.mapsFromOptions);

        this.$scope.$watch(() => this.staticProperty.properties.selectedProperty, (newValue, oldValue) => {
            if (newValue !== oldValue) {
                this.$rootScope.$emit(this.staticProperty.properties.internalName);
            }
        });

        if (!this.staticProperty.properties.selectedProperty) {
            this.staticProperty.properties.selectedProperty = this.availableProperties[0].properties.runtimeId;
            this.$timeout(() => {
                this.$rootScope.$emit(this.staticProperty.properties.internalName);
            }, 20);
        }
    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined ? [] : this.selectedElement.inputStreams[streamIndex].eventSchema.eventProperties;
    }

    findIndex() {
        let prefix = this.staticProperty.properties.mapsFromOptions[0].split("::");
        prefix = prefix[0].replace("s", "");
        return prefix;
    }
}

MappingUnaryController.$inject=['$scope', '$rootScope', 'PropertySelectorService', '$timeout'];