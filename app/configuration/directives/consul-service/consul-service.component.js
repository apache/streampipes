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
            ctrl.showConfiguration = false;
            ctrl.updateConfiguration = function () {
                ctrl.onUpdate({serviceDetails: this.serviceDetails});
            }
            ctrl.toggleConfiguration = function () {
                ctrl.showConfiguration = !ctrl.showConfiguration;
            }
        }
    }

};
