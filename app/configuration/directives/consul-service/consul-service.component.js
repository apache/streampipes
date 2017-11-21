consulService.$inject = [];

export default function consulService() {

    return {
        templateUrl: 'app/configuration/directives/consul-service/consul-service.tmpl.html',
        bindings: {
            configurations: "<"
        },
        controller: function () {
            var service = this.configurations;
        }
    }

};
