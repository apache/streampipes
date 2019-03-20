import {DatatypePropertyController} from "./datatype-property.controller";
declare const require: any;

export let DatatypePropertyComponent = {
    template: require('./datatype-property.tmpl.html'),
    bindings: {
        disabled : "<",
        runtimeType : "=",
        dpMode: "="
    },
    controller: DatatypePropertyController,
    controllerAs: 'ctrl'
};