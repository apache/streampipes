import {RequiredPropertyValuesController} from "./required-property-values.controller";
declare const require: any;

export let RequiredPropertyValuesComponent = {
    template: require('./required-property-values.tmpl.html'),
    bindings: {
        disabled : "<",
        property : "="
    },
    controller: RequiredPropertyValuesController,
    controllerAs: 'ctrl'
};