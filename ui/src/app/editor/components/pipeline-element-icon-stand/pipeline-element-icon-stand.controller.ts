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

import * as angular from 'angular';

export class PipelineElementIconStandController {

    RestApi: any;
    availableOptions: any;
    selectedOptions: any;
    options: any;
    activeType: any;
    currentElementName: any;
    EditorDialogManager: any;
    currentElements: any;
    $scope: any;

    constructor($scope, $rootScope, RestApi, EditorDialogManager) {
        this.RestApi = RestApi;
        this.availableOptions = [];
        this.selectedOptions = [];
        this.options = [];
        this.EditorDialogManager = EditorDialogManager;
        this.$scope = $scope;
    }

    $onInit() {
        this.loadOptions();

        this.$scope.$watch(() => this.activeType, () => {
            this.selectAllOptions();
        });
    }

    openHelpDialog(pipelineElement) {
        this.EditorDialogManager.openHelpDialog(pipelineElement);
    }

    updateMouseOver(elementName) {
        this.currentElementName = elementName;
    }

    loadOptions(type?) {
        this.RestApi.getEpCategories()
            .then(msg => {
                let s = msg;
                this.handleCategoriesSuccess("stream", s);
                this.handleCategoriesSuccess("set", s);
            });

        this.RestApi.getEpaCategories()
            .then(s => this.handleCategoriesSuccess("sepa", s));

        this.RestApi.getEcCategories()
            .then(s => this.handleCategoriesSuccess("action", s));

    };

    getOptions(type) {
        this.selectAllOptions();
        return this.availableOptions[type];
    }


    handleCategoriesSuccess(type, result) {
        this.availableOptions[type] = result.data;
        this.selectAllOptions();
    }

    toggleFilter(option) {
        this.selectedOptions = [];
        this.selectedOptions.push(option.type);
    }

    optionSelected(option) {
        return this.selectedOptions.indexOf(option.type) > -1;
    }

    selectAllOptions() {
        this.selectedOptions = [];
        angular.forEach(this.availableOptions[this.activeType], o => {
            this.selectedOptions.push(o.type);
        });
    }

    deselectAllOptions() {
        this.selectedOptions = [];
    }
}

PipelineElementIconStandController.$inject = ['$scope', '$rootScope', 'RestApi', 'EditorDialogManager'];