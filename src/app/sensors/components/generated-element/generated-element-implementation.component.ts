import {GeneratedElementImplementationController} from "./generated-element-implementation.controller";

export let GeneratedElementImplementationComponent = {
    templateUrl: 'generated-element-implementation.tmpl.html',
    bindings: {
        zipFile : "<",
        element: "<"
    },
    controller: GeneratedElementImplementationController,
    controllerAs: 'ctrl'
};