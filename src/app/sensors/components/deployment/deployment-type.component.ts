import {DeploymentTypeController} from "./deployment-type.controller";
declare const require: any;

export let DeploymentTypeComponent = {
    template: require('./deployment-type.tmpl.html'),
    bindings: {
        disabled : "<",
        deployment : "<",
    },
    controller: DeploymentTypeController,
    controllerAs: 'ctrl'
};