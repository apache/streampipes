import {DomainPropertyController} from "./domain-property.controller";

export let DomainPropertyComponent = {
    templateUrl: 'app/sensors/components/property/domain-property.tmpl.html',
    bindings: {
        disabled : "<",
        property : "<"
    },
    controller: DomainPropertyController,
    controllerAs: 'ctrl'
};