import {GeneratedElementImplementationController} from "./generated-element-implementation.controller";
declare const require: any;

export let GeneratedElementImplementationComponent = {
    template: require('./generated-element-implementation.tmpl.html'),
    bindings: {
        zipFile : "<",
        element: "<"
    },
    controller: GeneratedElementImplementationController,
    controllerAs: 'ctrl'
};