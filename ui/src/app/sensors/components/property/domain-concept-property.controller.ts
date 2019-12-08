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

export class DomainConceptPropertyController {

    RestApi: any;
    concepts: any;
    properties: any;

    constructor(RestApi) {
        this.RestApi = RestApi;
        this.concepts = [];
        this.properties = [];
    }

    $onInit() {
        this.loadProperties();
        this.loadConcepts();
    }

    loadProperties() {
        this.RestApi.getOntologyProperties()
            .then(propertiesData => {
                this.properties = propertiesData.data;
            });
    }

    loadConcepts() {
        this.RestApi.getOntologyConcepts()
            .then(conceptsData => {
                this.concepts = conceptsData.data;
            });
    }

    addSupportedProperty(supportedProperties) {
        if (supportedProperties == undefined) supportedProperties = [];
        supportedProperties.push({"propertyId": ""});
    }

    removeSupportedProperty(supportedProperties, index) {
        supportedProperties.splice(index, 1);
    }

    conceptRestricted(domainProperty) {
        return (domainProperty.requiredClass != undefined);
    }

    toggleConceptRestriction(domainProperty) {
        if (this.conceptRestricted(domainProperty)) domainProperty.requiredClass = undefined;
        else domainProperty.requiredClass = this.concepts[0].id;
    }

    conceptSelected(conceptId, currentConceptId) {
        return (conceptId == currentConceptId);
    }

    isSelectedProperty(availableProperty, selectedProperty) {
        return (availableProperty == selectedProperty);
    }

}

DomainConceptPropertyController.$inject = ['RestApi'];