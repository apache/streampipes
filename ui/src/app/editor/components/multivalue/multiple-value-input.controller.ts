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

export class MultipleValueInputController {

    constructor() {

    }

    addTextInputRow(members) {
        members.push({"input": {"type": "TextInput", "properties": {"description": "", "value": ""}}});
    }

    removeTextInputRow(members, property) {
        members.splice(property, 1);
    }

    addDomainConceptRow(firstMember, members) {
        var supportedProperties = [];
        angular.forEach(firstMember.input.properties.supportedProperties, property => {
            supportedProperties.push({"propertyId": property.propertyId, "value": ""});
        });
        members.push({"input": {"type": "DomainConceptInput",
            "properties": {
                "elementType": "DOMAIN_CONCEPT",
                "description": "",
                "supportedProperties": supportedProperties,
                "requiredClass": firstMember.input.properties.requiredClass
            }
        }
        });
    }

    removeDomainConceptRow(members, property) {
        members.splice(property, 1);
    }
}