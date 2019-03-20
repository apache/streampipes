import {AnyController} from "./any.controller";
declare const require: any;

export let AnyComponent = {
    template: require('./any.tmpl.html'),
    bindings: {
        staticProperty: "="
    },
    controller: AnyController,
    controllerAs: 'ctrl'
};
