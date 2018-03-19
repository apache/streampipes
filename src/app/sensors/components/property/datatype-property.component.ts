import {DatatypePropertyController} from "./datatype-property.controller";

export let DatatypePropertyComponent = {
    templateUrl: 'datatype-property.tmpl.html',
    bindings: {
        disabled : "<",
        runtimeType : "=",
        dpMode: "="
    },
    controller: DatatypePropertyController,
    controllerAs: 'ctrl'
};