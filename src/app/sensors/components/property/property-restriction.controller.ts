export class PropertyRestrictionController {

    constructor() {

    }

    addPropertyRestriction(key, restriction) {
        if (restriction.eventSchema.eventProperties == undefined) restriction.eventSchema.eventProperties = [];
        restriction.eventSchema.eventProperties.push({
            "type": "org.streampipes.model.schema.EventPropertyPrimitive",
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
        return "urn:fzi.de:sepa:" + this.randomString();
    }

    randomString() {
        var result = '';
        var chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        for (var i = 0; i < 12; i++) result += chars[Math.round(Math.random() * (chars.length - 1))];
        return result;
    }
}