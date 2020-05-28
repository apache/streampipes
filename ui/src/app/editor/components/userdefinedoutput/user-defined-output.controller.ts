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

import {RdfId} from "../../../platform-services/tsonld/RdfId";

export class UserDefinedOutputController {

    private prefix = "urn:streampipes.org:spi:";
    private chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    outputStrategy: any;
    selectedElement: any;
    collectedPropertiesFirstStream: any;
    collectedPropertiesSecondStream: any;
    customizeForm: any;

    primitiveClasses = [{"label": "String", "id": "http://www.w3.org/2001/XMLSchema#string"},
        {"label": "Boolean", "id": "http://www.w3.org/2001/XMLSchema#boolean"},
        {"label": "Integer", "id": "http://www.w3.org/2001/XMLSchema#integer"},
        {"label": "Long", "id": "http://www.w3.org/2001/XMLSchema#long"},
        {"label": "Double", "id": "http://www.w3.org/2001/XMLSchema#double"},
        {"label": "Float", "id": "http://www.w3.org/2001/XMLSchema#float"}];

    constructor() {

    }

    $onInit() {
        if (!this.outputStrategy.properties.eventProperties) {
            this.outputStrategy.properties.eventProperties = [];
        }
    }

    applyDefaultSchema() {
        this.outputStrategy.properties.eventProperties =
            this.selectedElement.inputStreams[0].eventSchema.eventProperties;
    }

    removeProperty(ep: any) {
        this.outputStrategy.properties.eventProperties
            .splice(this.outputStrategy.properties.eventProperties.indexOf(ep), 1);
    }

    addProperty() {
        this.outputStrategy.properties.eventProperties.push(this.makeDefaultProperty());
    }

    makeDefaultProperty() {
        let ep = {} as any;
        ep.type = "org.apache.streampipes.model.schema.EventPropertyPrimitive";
        ep.properties = {};
        ep.properties.domainProperties = [];
        ep.properties.elementId = "urn:streampipes.org:spi:eventpropertyprimitive:" + this.makeId();

        return ep;
    }

   makeId() {
        return this.prefix + this.randomString(6);
    }

    randomString(length) {
        let result = '';
        for (let i = length; i > 0; --i) result += this.chars[Math.floor(Math.random() * this.chars.length)];
        return result;
    }





}