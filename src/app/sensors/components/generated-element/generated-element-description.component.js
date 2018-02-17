import {GeneratedElementDescriptionController} from "./generated-element-description.controller";

export let GeneratedElementDescriptionComponent = {
    templateUrl: 'app/sensors/components/generated-element/generated-element-description.tmpl.html',
    bindings: {
        jsonld : "<",
        java : "<",
        element: "<"
    },
    controller: GeneratedElementDescriptionController,
    controllerAs: 'ctrl'
};