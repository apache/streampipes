export class DatatypePropertyController {

    constructor() {
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
                "title": "Long",
                "description": "A whole numerical datatype, e.g., '2332313993'",
                "id": "http://www.w3.org/2001/XMLSchema#long"
            },
            {
                "title": "Double",
                "description": "A floating-point number, e.g., '1.25'",
                "id": "http://www.w3.org/2001/XMLSchema#double"
            },
            {
                "title": "Float",
                "description": "A floating-point number, e.g., '1.25'",
                "id": "http://www.w3.org/2001/XMLSchema#float"
            }];


        if (this.dpMode == 'restriction')
            this.primitiveClasses.push({
                "title": "Number",
                "description": "Any numerical value",
                "id": "http://schema.org/Number"
            });
    }
    
}