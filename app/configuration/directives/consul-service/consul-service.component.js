consulService.$inject = [];

export default function consulService() {

    return {
        templateUrl: 'app/configuration/directives/consul-service/consul-service.tmpl.html',
        bindings: {
            serviceDetails: "<",
            onUpdate: "&"
        },
        controller: function ($scope) {
            var ctrl = this;
            $scope.serviceDetails = ctrl.serviceDetails;
            $scope.updateConfiguration = function() {
                ctrl.onUpdate({serviceDetails: $scope.serviceDetails});
            }
        }
    }

};
