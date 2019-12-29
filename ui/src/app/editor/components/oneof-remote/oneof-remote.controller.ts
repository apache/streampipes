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

export class OneOfRemoteController {

    RestApi: any;
    $rootScope: any;
    $scope: any;
    staticProperty: any;
    selectedElement: any;
    currentlyLinkedProperty: any;

    showOptions: boolean = false;

    dependentStaticProperties: any = new Map();
    loading: boolean = false;

    constructor(RestApi, $rootScope, $scope) {
        this.RestApi = RestApi;
        this.$rootScope = $rootScope;
        this.$scope = $scope;
    }

    $onInit() {
        if (this.staticProperty.properties.options.length == 0) {
            if ((!this.staticProperty.properties.dependsOn || this.staticProperty.properties.dependsOn.length == 0)) {
                this.loadOptionsFromRestApi();
            }
        } else {
            this.loadSavedProperty();

        }

        angular.forEach(this.selectedElement.staticProperties, sp => {
            if (sp.properties.internalName === this.staticProperty.properties.linkedMappingPropertyId) {
                this.currentlyLinkedProperty = sp.properties.mapsTo;
            }
        });

        if (this.staticProperty.properties.dependsOn && this.staticProperty.properties.dependsOn.length > 0 && !this.staticProperty.properties.currentSelection) {
            angular.forEach(this.staticProperty.properties.dependsOn, dp => {
                this.dependentStaticProperties.set(dp, false);
                this.$rootScope.$on(dp, (valid) => {
                    this.showOptions = false;
                    this.dependentStaticProperties.set(dp, true);
                    if (Array.from(this.dependentStaticProperties.values()).every(v => v === true)) {
                        this.loadOptionsFromRestApi();
                    }
                });
            });
        }

    }

    loadOptionsFromRestApi() {
        var resolvableOptionsParameterRequest = {};
        resolvableOptionsParameterRequest['staticProperties'] = this.selectedElement.staticProperties;
        resolvableOptionsParameterRequest['inputStreams'] = this.selectedElement.inputStreams;
        resolvableOptionsParameterRequest['belongsTo'] = this.selectedElement.belongsTo;
        resolvableOptionsParameterRequest['appId'] = this.selectedElement.appId;
        resolvableOptionsParameterRequest['runtimeResolvableInternalId'] = this.staticProperty.properties.internalName;

        this.showOptions = false;
        this.loading = true;
        this.RestApi.fetchRemoteOptions(resolvableOptionsParameterRequest).then(msg => {
            let data = msg.data;
            this.staticProperty.properties.options = data;
            if (this.staticProperty.properties.options.length > 0) {
                this.staticProperty.properties.options[0].selected = true;
            }
            this.loading = false;
            this.$rootScope.$emit(this.staticProperty.properties.internalName);
            this.loadSavedProperty();
        });
    }

    toggle() {
        this.staticProperty.properties.options.forEach(option => {
            if (option.name === this.staticProperty.properties.currentSelection.name) {
                option.selected = true;
            }  else {
                option.selected = false;
            }
        });
        this.$rootScope.$emit(this.staticProperty.properties.internalName);
    }

    loadSavedProperty() {
        angular.forEach(this.staticProperty.properties.options, option => {
            if (option.selected) {
                this.staticProperty.properties.currentSelection = option;
            }
        });
        this.showOptions = true;
    }
}

OneOfRemoteController.$inject = ['RestApi', '$rootScope', '$scope'];