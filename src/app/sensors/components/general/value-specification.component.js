import {ValueSpecificationController} from "./value-specification.controller";

export let ValueSpecificationComponent = {
    templateUrl: 'app/sensors/components/general/value-specification.tmpl.html',
    bindings: {
        disabled : "<",
        property : "<",
        runtimeType : "<"
    },
    controller: ValueSpecificationController,
    controllerAs: 'ctrl'
};