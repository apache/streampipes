export class StreamQualityDefinitionsController {

    qualities: any;
    property: any;

    constructor() {
        this.qualities = [{label: "Frequency", "description": "", "type": "org.streampipes.model.quality.Frequency"},
            {label: "Latency", "description": "", "type": "org.streampipes.model.quality.Latency"}];
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
    }
}