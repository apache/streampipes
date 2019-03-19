import {ValueSpecificationController} from "./value-specification.controller";
declare const require: any;

export let ValueSpecificationComponent = {
    template: require('./value-specification.tmpl.html'),
    bindings: {
        disabled : "<",
        property : "<",
        runtimeType : "<"
    },
    controller: ValueSpecificationController,
    controllerAs: 'ctrl'
};