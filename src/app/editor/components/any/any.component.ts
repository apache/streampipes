import {AnyController} from "./any.controller";

export let AnyComponent = {
    templateUrl: 'any.tmpl.html',
    bindings: {
        staticProperty: "="
    },
    controller: AnyController,
    controllerAs: 'ctrl'
};
