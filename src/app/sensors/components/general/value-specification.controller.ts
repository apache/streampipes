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