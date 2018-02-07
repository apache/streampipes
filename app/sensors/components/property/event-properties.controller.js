export class EventPropertiesController {

    constructor(restApi) {
        this.restApi = restApi;
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
        this.loadProperties();
    }

    loadProperties() {
        this.restApi.getOntologyProperties()
            .success(propertiesData => {
                this.existingProperties = propertiesData;
            })
            .error(msg => {
                console.log(msg);
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

EventPropertiesController.$inject = ['restApi'];