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

export class ValueSpecificationController {

    valueSpecifications: any;
    runtimeType: any;
    property: any;

    constructor() {
        this.valueSpecifications = [{label: "None", "type": undefined},
            {label: "Quantitative Value", "type": "org.streampipes.model.schema.QuantitativeValue"},
            {label: "Enumeration", "type": "org.streampipes.model.schema.Enumeration"}];
    }

    isDisallowed(type) {
        if ((type == this.valueSpecifications[1].type) && !this.isNumericalProperty()) return true;
        else if ((type == this.valueSpecifications[2].type) && this.isBoolean()) return true;
        else return false;
    }

    isNumericalProperty() {
        if (this.runtimeType != "http://www.w3.org/2001/XMLSchema#string" &&
            this.runtimeType != "http://www.w3.org/2001/XMLSchema#boolean") return true;
        else return false;

    }

    isBoolean() {
        if (this.runtimeType == "http://www.w3.org/2001/XMLSchema#boolean") return true;
        else return false;
    }

    add() {
        if (this.property.properties == undefined) {
            this.property.properties = {};
            this.property.properties.runtimeValues = [];
        }
        this.property.properties.runtimeValues.push("");
    }

    remove(runtimeValues, propertyIndex) {
        runtimeValues.splice(propertyIndex, 1);
    };
}