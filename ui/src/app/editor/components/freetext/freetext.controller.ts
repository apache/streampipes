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

import * as angular from 'angular';

export class FreeTextController {

    staticProperty: any;
    mappingProperty: any;
    selectedEventProperty: any;
    inputStreams: any;
    customizeForm: any;

    $scope: any;
    $rootScope: any;

    primitiveClasses = [{"id": "http://www.w3.org/2001/XMLSchema#string"},
        {"id": "http://www.w3.org/2001/XMLSchema#boolean"},
        {"id": "http://www.w3.org/2001/XMLSchema#integer"},
        {"id": "http://www.w3.org/2001/XMLSchema#long"},
        {"id": "http://www.w3.org/2001/XMLSchema#double"},
        {"id": "http://www.w3.org/2001/XMLSchema#float"}];

    constructor($scope, $rootScope) {
        this.$scope = $scope;
        this.$rootScope = $rootScope;
    }

    $onInit() {
        if (this.staticProperty.properties.valueSpecification) {
            this.staticProperty.properties.value = (this.staticProperty.properties.value * 1);
        }

        if (this.mappingProperty) {
            this.selectedEventProperty = this.updateCurrentEventProperty(this.mappingProperty.properties.mapsTo);
        }
    }

    notifyListeners() {
        this.$rootScope.$emit(
            this.staticProperty.properties.internalName,
            this.customizeForm[this.staticProperty.properties.internalName].$valid);
    }

    updateCurrentEventProperty(mapsTo) {
        var eventProperty;
        angular.forEach(this.inputStreams, stream => {
            angular.forEach(stream.eventSchema.eventProperties, property => {
                if (this.mappingProperty.properties.mapsTo == property.properties.elementId) {
                    eventProperty = property;
                }
            });
        });
        return eventProperty;
    }

    applyPlaceholder(runtimeName) {
        if (!this.staticProperty.properties.value) {
            this.staticProperty.properties.value = "";
        }
        this.staticProperty.properties.value = this.staticProperty.properties.value + "#" + runtimeName + "#" + " ";
    }

    getFriendlyDatatype() {
        if (this.staticProperty.properties.requiredDatatype == this.primitiveClasses[2].id ||
            this.staticProperty.properties.requiredDatatype == this.primitiveClasses[3].id ||
            this.staticProperty.properties.requiredDomainProperty == this.primitiveClasses[2].id ||
            this.staticProperty.properties.requiredDomainProperty == this.primitiveClasses[3].id) {
            return "The value should be a number (e.g., '1', '10'";
        } else if (this.staticProperty.properties.requiredDatatype == this.primitiveClasses[4].id ||
            this.staticProperty.properties.requiredDatatype == this.primitiveClasses[4].id ||
            this.staticProperty.properties.requiredDomainProperty == this.primitiveClasses[4].id) {
            return "The value should be a floating-point number (e.g., '1.0, '20.5')";
        } else {
            return "This value is not valid.";
        }
    }

}
