import {GeneratedElementDescriptionController} from "./generated-element-description.controller";
declare const require: any;

export let GeneratedElementDescriptionComponent = {
    template: require('./generated-element-description.tmpl.html'),
    bindings: {
        jsonld : "<",
        java : "<",
        element: "<"
    },
    controller: GeneratedElementDescriptionController,
    controllerAs: 'ctrl'
};