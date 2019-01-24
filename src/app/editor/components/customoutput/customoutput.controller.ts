export class CustomOutputController {

    outputStrategy: any;
    selectedElement: any;
    collectedPropertiesFirstStream: any;
    collectedPropertiesSecondStream: any;

    constructor(PropertySelectorService) {
        this.collectedPropertiesFirstStream = PropertySelectorService
            .makeProperties(this.getProperties(0), this.outputStrategy.properties.availablePropertyKeys, PropertySelectorService.firstStreamPrefix);
        this.collectedPropertiesSecondStream = PropertySelectorService
            .makeProperties(this.getProperties(1), this.outputStrategy.properties.availablePropertyKeys, PropertySelectorService.secondStreamPrefix);
    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined ? [] : this.selectedElement.inputStreams[streamIndex].eventSchema.eventProperties;
    }
}

CustomOutputController.$inject=['PropertySelectorService']