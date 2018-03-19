import {DomainConceptPropertyController} from "./domain-concept-property.controller";

export let DomainConceptPropertyComponent = {
    templateUrl: 'domain-concept-property.tmpl.html',
    bindings: {
        disabled : "<",
        domainProperty : "="
    },
    controller: DomainConceptPropertyController,
    controllerAs: 'ctrl'
};