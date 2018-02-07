import {DeploymentController} from "./deployment.controller";

export let DeploymentComponent = {
    templateUrl: 'app/sensors/components/deployment/deployment.tmpl.html',
    bindings: {
        disabled : "<",
        deploymentSettings : "<",
        element: "<"
    },
    controller: DeploymentController,
    controllerAs: 'ctrl'
};