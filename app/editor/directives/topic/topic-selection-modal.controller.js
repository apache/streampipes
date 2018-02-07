CustomizeController.$inject = ['$scope', '$rootScope', '$mdDialog', 'RestApi', 'state'];

export default function CustomizeController($scope, $rootScope, $mdDialog, RestApi, state) {

    $scope.streamDescription = state.data("JSON");
    $scope.finished = false;

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };

    var getMappings = function () {
        return getTopicDefinition()
            .wildcardTopicMappings;
    }

    var getMappingsByType = function (topicParameterType) {
        var result = [];
        angular.forEach(getMappings(), function (topicMapping) {
            if (topicMapping.topicParameterType == topicParameterType) {
                result.push(topicMapping);
            }
        });
        return result;
    }

    var getTopicDefinition = function () {
        return $scope.streamDescription.eventGrounding
            .transportProtocols[0]
            .properties.topicDefinition
            .properties;
    }

    $scope.save = function () {
        RestApi
            .updateStream($scope.streamDescription)
            .success(function (stream) {
                state.data("JSON", $.extend(true, {}, stream));
                $scope.hide();
            });
    }

    $scope.platformIdMappings = getMappingsByType("PLATFORM_IDENTIFIER");
    $scope.locationIdMappings = getMappingsByType("LOCATION_IDENTIFIER");
    $scope.sensorIdMappings = getMappingsByType("SENSOR_IDENTIFIER");

    $scope.availableMappings = getMappings();
    $scope.finished = true;

}