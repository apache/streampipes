export class MappingUnaryController {

    staticProperty: any;

    constructor($scope, $rootScope) {
        if (!this.staticProperty.properties.mapsTo) {
            this.staticProperty.properties.mapsTo = this.staticProperty.properties.mapsFromOptions[0].properties.elementId;
        }
        $scope.$watch(() => this.staticProperty.properties.mapsTo, () => {
            $rootScope.$emit(this.staticProperty.properties.internalName);
        });
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

MappingUnaryController.$inject=['$scope', '$rootScope']