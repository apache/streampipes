export class MappingUnaryController {

    constructor() {

    }

    selected(option, staticProperty) {
    if (!staticProperty.properties.mapsTo) {
        if (option.properties.elementId == staticProperty.properties.mapsFromOptions[0].properties.elementId) {
            return true;
        } else {
            return false;
        }
    } else {
        return option.properties.elementId == staticProperty.properties.mapsTo;
    }
}
}