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

import {AddEndpointController} from './dialogs/add-endpoint/add-endpoint.controller';
import {EndpointInstallationController} from './dialogs/endpoint-installation/endpoint-installation.controller';
import * as angular from 'angular';

declare const require: any;

export class AddCtrl {

    RestApi: any;
    $mdDialog: any;
    ElementIconText: any;
    elements: any;
    results: any;
    loading: any;
    endpointItems: any;
    endpointItemsLoadingComplete: any;
    selectedTab: any;
    $templateCache: any;
    availableTypes: {"source", "sepa", "action"};

    constructor(RestApi, $mdDialog, ElementIconText, $templateCache) {
        this.RestApi = RestApi;
        this.$mdDialog = $mdDialog;
        this.ElementIconText = ElementIconText;
        this.elements = "";
        this.results = [];
        this.loading = false;
        this.endpointItems = [];
        this.endpointItemsLoadingComplete = false;
        this.selectedTab = "MARKETPLACE";
        this.$templateCache = $templateCache;
        this.$templateCache.put('endpoint-item.tmpl.html', require('./endpoint-item.tmpl.html'));
    }

    $onInit() {
        this.getEndpointItems();
    }

    iconText(elementName) {
        return this.ElementIconText.getElementIconText(elementName);
    }

    setSelectedTab(type) {
        this.selectedTab = type;
    }

    isSelected(endpointItem) {
        return endpointItem.selected;
    }

    getSelectedBackground(endpointItem) {
        if (endpointItem.selected) return "#EEEEEE";
        else return "#FFFFFF";
    }

    toggleSelected(endpointItem) {
        endpointItem.selected = !endpointItem.selected;
    }

    selectAll(selected) {
        this.endpointItems.forEach(item => {
            if (item.type === this.selectedTab || this.selectedTab == 'all') item.selected = selected;
        });
    }

    getTitle(selectedTab) {
        if (selectedTab === 'source') {
            return "Data Sources";
        } else if (selectedTab === 'sepa') {
            return "Processing Elements";
        } else if (selectedTab === 'action') {
            return "Data Sinks";
        } else if (selectedTab === 'all') {
            return "All Pipeline Elements";
        } else {
            return "Marketplace";
        }
    }

    getItemTitle(selectedTab) {
        if (selectedTab === 'source') {
            return "Data Source";
        } else if (selectedTab === 'sepa') {
            return "Data Processor";
        } else {
            return "Data Sink";
        }
    }

    getItemStyle(type) {
        let baseType = "pe-label ";
        if (type == 'source') {
            return baseType + "source-label";
        } else if (type == 'sepa') {
            return baseType + "processor-label";
        } else {
            return baseType + "sink-label";
        }
    }
    showManageRdfEndpointsDialog() {
        this.$mdDialog.show({
            controller: AddEndpointController,
            controllerAs: 'ctrl',
            template: require('./dialogs/add-endpoint/add-endpoint.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                getEndpointItems: () => {
                    return this.getEndpointItems();
                }
            },
            bindToController: true
        })
    }

    getEndpointItems() {
        this.endpointItemsLoadingComplete = false;
        this.RestApi.getRdfEndpointItems()
            .then(msg => {
                let endpointItems = msg.data;
                this.endpointItems = endpointItems;
                this.endpointItemsLoadingComplete = true;
            });
    }

    addFromEndpoint(endpointUrl) {
        this.loading = true;
        this.RestApi.addBatch(endpointUrl, true)
            .then(msg => {
                var data = msg.data;
                this.loading = false;
                data.forEach((element, index) => {
                    this.results[index] = {};
                    this.results[index].success = element.success;
                    this.results[index].elementName = element.elementName;
                    this.results[index].details = [];
                    element.notifications.forEach(notification => {
                        let detail = {};
                        detail['description'] = notification.description;
                        detail['title'] = notification.title;
                        this.results[index].details.push(detail);
                    })
                });
            })
    }

    installSingleElement(endpointItem) {
        let endpointItems = [];
        endpointItems.push(endpointItem);
        this.installElements(endpointItems, true);
    }

    uninstallSingleElement(endpointItem) {
        let endpointItems = [];
        endpointItems.push(endpointItem);
        this.installElements(endpointItems, false);
    }

    installSelected() {
        this.installElements(this.getSelectedElements(true), true);
    }

    uninstallSelected() {
        this.installElements(this.getSelectedElements(false), false);
    }

    getSelectedElements(install) {
        let elementsToInstall = [];

        this.endpointItems.forEach(item => {
            if (item.type === this.selectedTab || this.selectedTab == 'all') {
                if (item.installed === !install && item.selected) {
                    elementsToInstall.push(item);
                }
            }
        });

        return elementsToInstall;
    }

    installElements(endpointItems, install) {
        this.$mdDialog.show({
            controller: EndpointInstallationController,
            controllerAs: 'ctrl',
            template: require('./dialogs/endpoint-installation/endpoint-installation.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            locals: {
                endpointItems: endpointItems,
                install: install,
                getEndpointItems: () => {
                    return this.getEndpointItems();
                }
            },
            bindToController: true
        });
    }

}

AddCtrl.$inject = ['RestApi', '$mdDialog', 'ElementIconText', '$templateCache'];