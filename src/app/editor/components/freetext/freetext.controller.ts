import * as angular from 'angular';

export class FreeTextController {

    staticProperty: any;
    mappingProperty: any;
    selectedEventProperty: any;
    inputStreams: any;

    constructor() {
        if (this.staticProperty.properties.valueSpecification) {
            this.staticProperty.properties.value = (this.staticProperty.properties.value * 1);
        }

        if (this.mappingProperty) {
            this.selectedEventProperty = this.updateCurrentEventProperty(this.mappingProperty.properties.mapsTo);
        }

        // this.$watchCollection('mappingProperty.properties', (newValue, oldValue) => {
        //     this.selectedEventProperty = this.updateCurrentEventProperty(this.mappingProperty.properties.mapsTo);
        // });
    }


    updateCurrentEventProperty(mapsTo) {
        var eventProperty;
        angular.forEach(this.inputStreams, stream => {
            angular.forEach(stream.eventSchema.eventProperties, property => {
                if (this.mappingProperty.properties.mapsTo == property.properties.elementId) {
                    eventProperty = property;
                }
            });
        });
        return eventProperty;
    }
}
