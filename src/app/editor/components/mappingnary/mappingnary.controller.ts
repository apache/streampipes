export class MappingNaryController {

    constructor() {

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
        return staticProperty.properties.mapsTo.indexOf(property.properties.elementId) > -1;
    }

    add(property, staticProperty) {
        if (!staticProperty.properties.mapsTo) {
            staticProperty.properties.mapsTo = [];
        }
        staticProperty.properties.mapsTo.push(property.properties.elementId);
    }

    remove(property, staticProperty) {
        var index = staticProperty.properties.mapsTo.indexOf(property.properties.elementId);
        staticProperty.properties.mapsTo.splice(index, 1);
    }

}