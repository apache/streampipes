import {OneOfController} from "./oneof.controller";
declare const require: any;

export let OneOfComponent = {
    template: require('./oneof.tmpl.html'),
    bindings: {
        staticProperty : "="
    },
    controller: OneOfController,
    controllerAs: 'ctrl'
};
