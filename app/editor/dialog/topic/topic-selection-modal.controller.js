export class TopicSelectionDialog {

    constructor($mdDialog, RestApi, streamDescription) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;

        this.streamDescription = streamDescription;
        this.finished = false;

        this.platformIdMappings = this.getMappingsByType("PLATFORM_IDENTIFIER");
        this.locationIdMappings = this.getMappingsByType("LOCATION_IDENTIFIER");
        this.sensorIdMappings = this.getMappingsByType("SENSOR_IDENTIFIER");

        this.availableMappings = this.getMappings();
        this.finished = true;
    }

    hide() {
        this.$mdDialog.hide();
    }

    cancel() {
        this.$mdDialog.cancel();
    }

    getMappings() {
        return this.getTopicDefinition()
            .wildcardTopicMappings;
    }

    getMappingsByType(topicParameterType) {
        var result = [];
        angular.forEach(this.getMappings(), topicMapping => {
            if (topicMapping.topicParameterType == topicParameterType) {
                result.push(topicMapping);
            }
        });
        return result;
    }

    getTopicDefinition() {
        return this.streamDescription.eventGrounding
            .transportProtocols[0]
            .properties.topicDefinition
            .properties;
    }

    save() {
        this.RestApi
            .updateStream(this.streamDescription)
            .success(stream => {
                this.streamDescription = stream;
                this.hide();
            });
    }
}

TopicSelectionDialog.$inject = ['$mdDialog', 'RestApi', 'streamDescription'];