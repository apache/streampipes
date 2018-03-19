import {RequiredPropertyValuesController} from "./required-property-values.controller";

export let RequiredPropertyValuesComponent = {
    templateUrl: 'required-property-values.tmpl.html',
    bindings: {
        disabled : "<",
        property : "="
    },
    controller: RequiredPropertyValuesController,
    controllerAs: 'ctrl'
};