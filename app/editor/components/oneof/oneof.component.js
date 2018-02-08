import {OneOfController} from "./oneof.controller";

export let OneOfComponent = {
    templateUrl: 'app/editor/components/oneof/oneof.tmpl.html',
    bindings: {
        staticProperty : "="
    },
    controller: OneOfController,
    controllerAs: 'ctrl'
};
