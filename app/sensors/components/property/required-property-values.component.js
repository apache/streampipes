import {RequiredPropertyValuesController} from "./required-property-values.controller";

export let RequiredPropertyValuesComponent = {
    templateUrl: 'app/sensors/components/property/required-property-values.tmpl.html',
    bindings: {
        disabled : "<",
        property : "<"
    },
    controller: RequiredPropertyValuesController,
    controllerAs: 'ctrl'
};