import {AnyController} from "./any.controller";

export let AnyComponent = {
    templateUrl: 'app/editor/components/any/any.tmpl.html',
    bindings: {
        staticProperty: "="
    },
    controller: AnyController,
    controllerAs: 'ctrl'
};
