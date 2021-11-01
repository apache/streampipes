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

export class PropertyRestrictionController {

    constructor() {

    }

    addPropertyRestriction(key, restriction) {
        if (restriction.eventSchema.eventProperties == undefined) restriction.eventSchema.eventProperties = [];
        restriction.eventSchema.eventProperties.push({
            "type": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
            "properties": {"elementName": this.makeElementName(), "runtimeType": "", "domainProperties": []}
        });
    }

    datatypeRestricted(property) {
        return (property.properties.runtimeType != undefined);
    }

    toggleDatatypeRestriction(property) {
        if (this.datatypeRestricted(property)) property.properties.runtimeType = undefined;
        else property.properties.runtimeType = "";
    }

    measurementUnitRestricted(property) {
        return (property.properties.measurementUnit != undefined);
    }

    toggleMeasurementUnitRestriction(property) {
        if (this.measurementUnitRestricted(property)) property.properties.measurementUnit = undefined;
        else property.properties.measurementUnit = "";
    }

    domainPropertyRestricted(property) {
        return (property.properties.domainProperties != undefined && property.properties.domainProperties[0] != undefined);
    }

    toggleDomainPropertyRestriction(property) {
        if (this.domainPropertyRestricted(property)) {
            property.properties.domainProperties = [];
        }
        else {
            property.properties.domainProperties = [];
            property.properties.domainProperties[0] = "";
        }
    }

    makeElementName() {
        return "urn:streampipes.apache.org:sepa:" + this.randomString();
    }

    randomString() {
        var result = '';
        var chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        for (var i = 0; i < 12; i++) result += chars[Math.round(Math.random() * (chars.length - 1))];
        return result;
    }
}
