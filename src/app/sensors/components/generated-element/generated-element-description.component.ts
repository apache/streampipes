import {GeneratedElementDescriptionController} from "./generated-element-description.controller";

export let GeneratedElementDescriptionComponent = {
    templateUrl: 'generated-element-description.tmpl.html',
    bindings: {
        jsonld : "<",
        java : "<",
        element: "<"
    },
    controller: GeneratedElementDescriptionController,
    controllerAs: 'ctrl'
};