import {DeploymentTypeController} from "./deployment-type.controller";

export let DeploymentTypeComponent = {
    templateUrl: 'app/sensors/components/deployment/deployment-type.tmpl.html',
    bindings: {
        disabled : "<",
        deployment : "<",
    },
    controller: DeploymentTypeController,
    controllerAs: 'ctrl'
};