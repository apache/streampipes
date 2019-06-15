export class CustomOutputController {

    outputStrategy: any;
    selectedElement: any;
    collectedPropertiesFirstStream: any;
    collectedPropertiesSecondStream: any;
    customizeForm: any;

    PropertySelectorService: any;

    constructor(PropertySelectorService) {
        this.PropertySelectorService = PropertySelectorService;
    }

    $onInit() {
        this.collectedPropertiesFirstStream = this.PropertySelectorService
            .makeProperties(this.getProperties(0), this.outputStrategy.properties.availablePropertyKeys, this.PropertySelectorService.firstStreamPrefix);
        this.collectedPropertiesSecondStream = this.PropertySelectorService
            .makeProperties(this.getProperties(1), this.outputStrategy.properties.availablePropertyKeys, this.PropertySelectorService.secondStreamPrefix);
    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined ? [] : this.selectedElement.inputStreams[streamIndex].eventSchema.eventProperties;
    }

    selectAll(collectedProperties) {
        collectedProperties.forEach(ep => this.outputStrategy.properties.selectedPropertyKeys.push(ep.properties.runtimeId));
        // This is needed to trigger update of scope
        this.outputStrategy.properties.selectedPropertyKeys = this.outputStrategy.properties.selectedPropertyKeys.filter(el => {return true;});
    }

    deselectAll(collectedProperties) {
        collectedProperties.forEach(ep => this.outputStrategy.properties.selectedPropertyKeys =
            this.outputStrategy.properties.selectedPropertyKeys.filter(item => item !== ep.properties.runtimeId));

    }
}

CustomOutputController.$inject = ['PropertySelectorService']