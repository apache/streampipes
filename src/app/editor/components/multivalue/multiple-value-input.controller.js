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