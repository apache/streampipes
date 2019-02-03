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
        if (!staticProperty.properties.selectedProperties) return false;
        return staticProperty.properties.selectedProperties.indexOf(property.properties.runtimeId) > -1;
    }

    add(property, staticProperty) {
        if (!staticProperty.properties.selectedProperties) {
            staticProperty.properties.selectedProperties = [];
        }
        staticProperty.properties.selectedProperties.push(property.properties.runtimeId);
    }

    remove(property, staticProperty) {
        var index = staticProperty.properties.selectedProperties.indexOf(property.properties.runtimeId);
        staticProperty.properties.selectedProperties.splice(index, 1);
    }

    selectAll() {
        this.staticProperty.properties.selectedProperties = this.staticProperty.properties.mapsFromOptions;
    }

    deselectAll() {
        this.staticProperty.properties.selectedProperties = [];
    }
}

MappingNaryController.$inject=['PropertySelectorService']