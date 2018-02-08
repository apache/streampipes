export class TopicSelectionDialogController {

    constructor() {


        angular.forEach(this.topicMappings, topicMapping => {
            if (!topicMapping.selectedMapping) {
                topicMapping.selectedMapping = "*";
            }
        })

        this.eventProperties = this.getEventProperties();
    }

    getEventProperties() {
        return this.streamDescription.eventSchema.eventProperties;
    }

    getEventProperty(runtimeName) {
        var eventProperty = {};
        angular.forEach(this.getEventProperties(), function (ep) {
            if (ep.properties.runtimeName == runtimeName) {
                eventProperty = ep;
            }
        })
        return eventProperty;
    }

    getTopicDefinition() {
        return this.streamDescription.eventGrounding
            .transportProtocols[0]
            .properties.topicDefinition
            .properties;
    }

    getWildcardTopic() {
        return this.getTopicDefinition()
            .wildcardTopicName;
    }

    getLabel(runtimeName) {
        var ep = this.getEventProperty(runtimeName);
        return ep.properties.label + " (" + ep.properties.description + ")";
    }

    getPossibleValues(runtimeName) {
        var ep = this.getEventProperty(runtimeName);
        return ep.properties.valueSpecification.properties.runtimeValues;
    }


}