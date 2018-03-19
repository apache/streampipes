export class PropertyQualityDefinitionsController {

    qualities: any;
    property: any;

    constructor() {
        this.qualities = [{label: "Accuracy", "description": "", "type": "org.streampipes.model.quality.Accuracy"},
            {label: "Precision", "description": "", "type": "org.streampipes.model.quality.Precision"},
            {label: "Resolution", "description": "", "type": "org.streampipes.model.quality.Resolution"}];
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