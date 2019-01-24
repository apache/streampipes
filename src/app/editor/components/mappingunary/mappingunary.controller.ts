export class MappingUnaryController {

    staticProperty: any;
    selectedElement: any;
    availableProperties: any;

    constructor($scope, $rootScope, PropertySelectorService) {
        this.availableProperties = PropertySelectorService.makeFlatProperties(this.getProperties(this.findIndex()), this.staticProperty.properties.mapsFromOptions);
        if (!this.staticProperty.properties.selectedProperty) {
            this.staticProperty.properties.selectedProperty = this.availableProperties[0].properties.runtimeId;
        }
        $scope.$watch(() => this.staticProperty.properties.selectedProperty, () => {
            $rootScope.$emit(this.staticProperty.properties.internalName);
        });
    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined ? [] : this.selectedElement.inputStreams[streamIndex].eventSchema.eventProperties;
    }

    findIndex() {
        let prefix = this.staticProperty.properties.mapsFromOptions[0].split("::");
        prefix = prefix[0].replace("s", "");
        return prefix;
    }
}

MappingUnaryController.$inject=['$scope', '$rootScope', 'PropertySelectorService']