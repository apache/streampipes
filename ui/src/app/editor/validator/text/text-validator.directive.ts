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

export class TextValidatorDirective {

    textValidator: any;
    staticProperty: any;
    restrict: any;
    require: any;

    primitiveClasses = [{"id": "http://www.w3.org/2001/XMLSchema#string"},
        {"id": "http://www.w3.org/2001/XMLSchema#boolean"},
        {"id": "http://www.w3.org/2001/XMLSchema#integer"},
        {"id": "http://www.w3.org/2001/XMLSchema#long"},
        {"id": "http://www.w3.org/2001/XMLSchema#double"}];

    constructor() {
        this.restrict = 'A';
        this.require = 'ngModel';
    }

    link(scope, elm, attrs, ctrl) {
        scope.$watch(attrs.textValidator, newVal => {
            ctrl.$validators.textValidator = (modelValue, viewValue) => this.validateText(modelValue, viewValue, newVal);
        });
    }

    validateText(modelValue, viewValue, sp) {
        if (modelValue === "" || modelValue === undefined) {
            return false;
        } else {
            if (sp.properties.requiredDatatype) {
                return this.typeCheck(modelValue, sp.properties.requiredDatatype);
            } else if (sp.properties.requiredDomainProperty) {
                // TODO why is type info stored in required domain property??
                return this.typeCheck(modelValue, sp.properties.requiredDomainProperty);
            } else {
                return true;
            }
        }
    }

    typeCheck(property, datatype) {
        if (datatype == this.primitiveClasses[0].id) return true;
        if (datatype == this.primitiveClasses[1].id) return (property == 'true' || property == 'false');
        if (datatype == this.primitiveClasses[2].id) return (!isNaN(property) && parseInt(Number(property) + '') == property && !isNaN(parseInt(property, 10)));
        if (datatype == this.primitiveClasses[3].id) return (!isNaN(property) && parseInt(Number(property) + '') == property && !isNaN(parseInt(property, 10)));
        if (datatype == this.primitiveClasses[4].id) return !isNaN(property);
        return false;
    }
}