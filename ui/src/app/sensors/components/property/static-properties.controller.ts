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

export class StaticPropertiesController {

    RestApi: any;
    staticPropertyTypes: any;
    newStaticPropertyType: any;
    memberTypeSelected: any;
    properties: any;

    constructor(RestApi) {
        this.RestApi = RestApi;
        this.staticPropertyTypes = [{
            label: "Text Input",
            "type": "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty"
        },
            {label: "Single-Value Selection", "type": "org.apache.streampipes.model.staticproperty.OneOfStaticProperty"},
            {label: "Multi-Value Selection", "type": "org.apache.streampipes.model.staticproperty.AnyStaticProperty"},
            {label: "Domain Concept", "type": "org.apache.streampipes.model.staticproperty.DomainStaticProperty"},
            {
                label: "Single-Value Mapping Property",
                "type": "org.apache.streampipes.model.staticproperty.MappingPropertyUnary"
            },
            {label: "Multi-Value Mapping Property", "type": "org.apache.streampipes.model.staticproperty.MappingPropertyNary"},
            {label: "Collection", "type": "org.apache.streampipes.model.staticproperty.CollectionStaticProperty"}];

        this.newStaticPropertyType = this.staticPropertyTypes[0].type;
        this.memberTypeSelected = false;
        this.properties = [];
    }

    $onInit() {
        this.loadProperties();
    }

    range(count) {
        return new Array(+count);
    }

    isSelectedProperty(mapsFrom, property) {
        return (property.properties.elementName == mapsFrom);
    };

    addStaticProperty(staticProperties, type) {
        if (staticProperties == undefined) staticProperties = [];
        staticProperties.push(this.getNewStaticProperty(type));
    }

    getNewStaticProperty(type) {
        if (type === this.staticPropertyTypes[0].type)
            return {"type": this.staticPropertyTypes[0].type, "properties": {"label": "", "description": ""}};
        else if (type === this.staticPropertyTypes[1].type)
            return {
                "type": this.staticPropertyTypes[1].type,
                "properties": {"label": "", "description": "", "options": []}
            };
        else if (type === this.staticPropertyTypes[2].type)
            return {
                "type": this.staticPropertyTypes[2].type,
                "properties": {"label": "", "description": "", "options": []}
            };
        else if (type === this.staticPropertyTypes[3].type)
            return {
                "type": this.staticPropertyTypes[3].type,
                "properties": {"label": "", "description": "", "supportedProperties": []}
            };
        else if (type === this.staticPropertyTypes[4].type)
            return {"type": this.staticPropertyTypes[4].type, "properties": {"label": "", "description": ""}};
        else if (type === this.staticPropertyTypes[5].type)
            return {"type": this.staticPropertyTypes[5].type, "properties": {"label": "", "description": ""}};
        else if (type === this.staticPropertyTypes[6].type)
            return {
                "type": this.staticPropertyTypes[6].type,
                "properties": {"label": "", "description": "", "memberType": "", "members": []}
            };
    }

    getType(property) {
        var label;
        angular.forEach(this.staticPropertyTypes, function (value) {
            if (value.type == property.type) label = value.label;
        });
        return label;
    };

    domainPropertyRestricted(property) {
        return (property.type != undefined);
    };

    toggleDomainPropertyRestriction(property) {
        if (property.type != undefined) property.type = undefined;
        else property.type = this.properties[0].id;
    }

    addMember(property) {
        property.members.push(angular.copy(this.getNewStaticProperty(property.memberType)));
        this.memberTypeSelected = true;
    }

    removeMember(property) {
        property.members = [];
        property.memberType = '';
        this.memberTypeSelected = false;
    }

    loadProperties() {
        this.RestApi.getOntologyProperties()
            .then(propertiesData =>  {
                this.properties = propertiesData.data;
            });
    }
}

StaticPropertiesController.$inject = ['RestApi'];