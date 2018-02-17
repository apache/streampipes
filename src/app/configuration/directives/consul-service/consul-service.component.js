import {ConsulServiceController} from "./consul-service.controller";

export let ConsulServiceComponent = {
    templateUrl: 'app/configuration/directives/consul-service/consul-service.tmpl.html',
    bindings: {
        serviceDetails: "<",
        onUpdate: "&"
    },
    controller: ConsulServiceController,
    controllerAs: 'ctrl'
};