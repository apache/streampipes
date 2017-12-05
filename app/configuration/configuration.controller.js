ConfigurationCtrl.$inject = ['$scope', 'ConfigurationRestService'];

export default function ConfigurationCtrl($scope, ConfigurationRestService) {

    $scope.services = {}

    var ctrl = this;

    $scope.getConfigurations = function () {
        ConfigurationRestService.get().then(function (services) {
            for (let service of services.data) {
                for (let config of service.configs) {
                    if (config.valueType === 'xs:integer') {
                        config.value = parseInt(config.value);
                    } else if (config.valueType === 'xs:double') {
                        config.value = parseFloat('xs:double');
                    } else if (config.valueType === 'xs:boolean') {
                        config.value = (config.value === 'true');
                    }
                }
            }
            $scope.services = services.data
        });
    }

    $scope.updateConfiguration = function (serviceDetails) {
        let serviceDetailsToString = angular.copy(serviceDetails);
        for (let config of serviceDetailsToString.configs) {
            config.value = config.value + '';
        }
        ConfigurationRestService.update(serviceDetailsToString).then(function (response) {

        });
    }

    $scope.getConfigurations()

}