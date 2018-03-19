import {ValueSpecificationController} from "./value-specification.controller";

export let ValueSpecificationComponent = {
    templateUrl: 'value-specification.tmpl.html',
    bindings: {
        disabled : "<",
        property : "<",
        runtimeType : "<"
    },
    controller: ValueSpecificationController,
    controllerAs: 'ctrl'
};