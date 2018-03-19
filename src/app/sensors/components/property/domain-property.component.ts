import {DomainPropertyController} from "./domain-property.controller";

export let DomainPropertyComponent = {
    templateUrl: 'domain-property.tmpl.html',
    bindings: {
        disabled : "<",
        property : "="
    },
    controller: DomainPropertyController,
    controllerAs: 'ctrl'
};