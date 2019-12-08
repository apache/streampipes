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

export class EventPropertiesController {

    RestApi: any;
    primitiveClasses: any;
    existingProperties: any;

    constructor(RestApi) {
        this.RestApi = RestApi;
        this.primitiveClasses = [{
            "title": "String",
            "description": "A textual datatype, e.g., 'machine1'",
            "id": "http://www.w3.org/2001/XMLSchema#string"
        },
            {"title": "Boolean", "description": "A true/false value", "id": "http://www.w3.org/2001/XMLSchema#boolean"},
            {
                "title": "Integer",
                "description": "A whole-numerical datatype, e.g., '1'",
                "id": "http://www.w3.org/2001/XMLSchema#integer"
            },
            {
                "title": "Double",
                "description": "A floating-point number, e.g., '1.25'",
                "id": "http://www.w3.org/2001/XMLSchema#double"
            }];

        this.existingProperties = [];
    }

    $onInit() {
        this.loadProperties();
    }

    loadProperties() {
        this.RestApi.getOntologyProperties()
            .then(propertiesData => {
                this.existingProperties = propertiesData.data;
            });
    }

    addProperty(properties) {
        if (properties == undefined) properties = [];
        properties.push({
            "type": "org.streampipes.model.schema.EventPropertyPrimitive",
            "properties": {"runtimeType": "", "domainProperties": [""]}
        });
    }
}

EventPropertiesController.$inject = ['RestApi'];