import {OneOfController} from "./oneof.controller";

export let OneOfComponent = {
    templateUrl: 'oneof.tmpl.html',
    bindings: {
        staticProperty : "="
    },
    controller: OneOfController,
    controllerAs: 'ctrl'
};
