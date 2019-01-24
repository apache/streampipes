export class MappingNaryController {

    staticProperty: any;
    selectedElement: any;
    availableProperties: any;

    constructor(PropertySelectorService) {
        this.availableProperties = PropertySelectorService.makeFlatProperties(this.getProperties(this.findIndex()), this.staticProperty.properties.mapsFromOptions);
    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined ? [] : this.selectedElement.inputStreams[streamIndex].eventSchema.eventProperties;
    }

    findIndex() {
        let prefix = this.staticProperty.properties.mapsFromOptions[0].split("::");
        prefix = prefix[0].replace("s", "");
        return prefix;
    }

    toggle(property, staticProperty) {
        if (this.exists(property, staticProperty)) {
            this.remove(property, staticProperty);
        } else {
            this.add(property, staticProperty);
        }
    }

    exists(property, staticProperty) {
        if (!staticProperty.properties.mapsTo) return false;
        return staticProperty.properties.mapsTo.indexOf(property.properties.runtimeId) > -1;
    }

    add(property, staticProperty) {
        if (!staticProperty.properties.mapsTo) {
            staticProperty.properties.mapsTo = [];
        }
        staticProperty.properties.mapsTo.push(property.properties.runtimeId);
    }

    remove(property, staticProperty) {
        var index = staticProperty.properties.mapsTo.indexOf(property.properties.runtimeId);
        staticProperty.properties.mapsTo.splice(index, 1);
    }
}

MappingNaryController.$inject=['PropertySelectorService']