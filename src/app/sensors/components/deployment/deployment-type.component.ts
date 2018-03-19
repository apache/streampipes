import {DeploymentTypeController} from "./deployment-type.controller";

export let DeploymentTypeComponent = {
    templateUrl: 'deployment-type.tmpl.html',
    bindings: {
        disabled : "<",
        deployment : "<",
    },
    controller: DeploymentTypeController,
    controllerAs: 'ctrl'
};