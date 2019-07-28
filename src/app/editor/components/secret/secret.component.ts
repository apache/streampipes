import {SecretController} from "./secret.controller";
declare const require: any;

export let SecretComponent = {
    template: require('./secret.tmpl.html'),
    bindings: {
        staticProperty: "=",
        customizeForm: "="
    },
    controller: SecretController,
    controllerAs: 'ctrl'
};
