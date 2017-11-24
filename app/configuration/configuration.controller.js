ConfigurationCtrl.$inject = ['$scope', 'ConfigurationRestService'];

export default function ConfigurationCtrl($scope, ConfigurationRestService) {

    $scope.services = {}

    var ctrl = this;

    $scope.getConfigurations = function () {
        ConfigurationRestService.get().then(function (services) {
            $scope.services = services.data
        });
    }

    $scope.updateConfiguration = function (serviceDetails) {
        ConfigurationRestService.update(serviceDetails).then(function (response) {

        });
    }

    $scope.getConfigurations()

}