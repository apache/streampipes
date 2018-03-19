import {DeploymentController} from "./deployment.controller";

export let DeploymentComponent = {
    templateUrl: 'deployment.tmpl.html',
    bindings: {
        disabled : "<",
        deploymentSettings : "=",
        element: "="
    },
    controller: DeploymentController,
    controllerAs: 'ctrl'
};