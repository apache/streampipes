ConfigurationCtrl.$inject = ['$scope', 'ConfigurationRestService'];

export default function ConfigurationCtrl($scope, ConfigurationRestService) {

    $scope.services = {}

    $scope.getConfigurations = function() {
        ConfigurationRestService.get().then(function(services) {
            $scope.services = services
        });
    }

    $scope.getConfigurations()

}