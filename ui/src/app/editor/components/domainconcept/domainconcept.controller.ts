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

export class DomainConceptController {

    RestApi: any;
    availableDomainProperties: any;
    autoCompleteStaticProperty: any;

    constructor(RestApi) {
        this.RestApi = RestApi;
        this.availableDomainProperties = {};
    }

    $onInit() {
        this.loadDomainConcepts(this.autoCompleteStaticProperty);
    }

    loadDomainConcepts(item) {
        var query = {};
        query['requiredClass'] = item.properties.requiredClass;
        query['requiredProperties'] = [];
        angular.forEach(item.properties.supportedProperties, function (p) {
            var propertyObj = {};
            propertyObj['propertyId'] = p.propertyId;
            query['requiredProperties'].push(propertyObj);
        });

        this.RestApi.getDomainKnowledgeItems(query)
            .then(msg => {
                let queryResponse = msg.data;
                if (!this.availableDomainProperties[item.elementId]) {
                    this.availableDomainProperties[item.elementId] = {};
                }
                angular.forEach(queryResponse.requiredProperties, resp => {
                    angular.forEach(resp.queryResponse, instanceResult => {
                        if (!this.availableDomainProperties[item.elementId][resp.propertyId])
                            this.availableDomainProperties[item.elementId][resp.propertyId] = [];
                        var instanceData = {
                            label: instanceResult.label,
                            description: instanceResult.description,
                            propertyValue: instanceResult.propertyValue
                        };
                        this.availableDomainProperties[item.elementId][resp.propertyId].push(instanceData);
                    });
                });
            });
    }

    querySearch(query, staticPropertyId) {
        var result = [];
        var i = 0;
        angular.forEach(this.availableDomainProperties[staticPropertyId], function (values) {
            if (values.length > 0 && i == 0) {
                var position = 0;
                angular.forEach(values, value => {
                    if (query == undefined || value.label.substring(0, query.length) === query) result.push({
                        label: value.label,
                        description: value.description,
                        position: position
                    });
                    position++;
                })
                i++;
            }
        });
        return result;
    }

    searchTextChange(text) {

    }

    selectedItemChange(item, staticPropertyId, supportedProperties) {
        angular.forEach(supportedProperties, supportedProperty => {
            supportedProperty.value = this.availableDomainProperties[staticPropertyId][supportedProperty.propertyId][item.position].propertyValue;
        });
    }

}