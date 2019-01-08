import * as angular from 'angular';

export class CustomOutputController {

    outputStrategy: any;
    selectedElement: any;
    collectedPropertiesFirstStream: any;
    collectedPropertiesSecondStream: any;

    firstStreamPrefix: string = "s0";
    secondStreamPrefix: string = "s1";
    propertyDelimiter: string = "::";

    constructor() {
        console.log(this.outputStrategy);
        this.collectedPropertiesFirstStream = this.makeProperties(this.getProperties(0), this.outputStrategy.properties.availablePropertyKeys, this.firstStreamPrefix);
        this.collectedPropertiesSecondStream = this.makeProperties(this.getProperties(1), this.outputStrategy.properties.availablePropertyKeys, this.secondStreamPrefix);
    }

    makeProperties(eventProperties, availablePropertyKeys, currentPointer) {
        var outputProperties = [];

        eventProperties.forEach(ep => {
            availablePropertyKeys.forEach(apk => {
                if (this.isInSelection(ep, apk, currentPointer)) {
                    ep.properties.runtimeId = this.makeSelector(currentPointer, ep.properties.runtimeName);
                    if (this.isNested(ep)) {
                        ep.properties.eventProperties = this.makeProperties(ep.properties.eventProperties, availablePropertyKeys, this.makeSelector(currentPointer, ep.properties.runtimeName));
                    }
                    outputProperties.push(ep);
                }
            });
        });
        return outputProperties;
    }

    isNested(ep) {
        return ep.type === "org.streampipes.model.schema.EventPropertyNested";
    }

    isInSelection(inputProperty, propertySelector, currentPropertyPointer) {
        return (currentPropertyPointer
            + this.propertyDelimiter
            + inputProperty.properties.runtimeName) === propertySelector;

    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined ? [] : this.selectedElement.inputStreams[streamIndex].eventSchema.eventProperties;
    }

    makeSelector(prefix, current) {
        return prefix + this.propertyDelimiter + current;
    }
}