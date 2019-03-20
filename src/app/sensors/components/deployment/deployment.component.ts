import {DeploymentController} from "./deployment.controller";
declare const require: any;

export let DeploymentComponent = {
    template: require('./deployment.tmpl.html'),
    bindings: {
        disabled : "<",
        deploymentSettings : "=",
        element: "="
    },
    controller: DeploymentController,
    controllerAs: 'ctrl'
};