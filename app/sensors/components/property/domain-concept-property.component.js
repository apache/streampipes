import {DomainConceptPropertyController} from "./domain-concept-property.controller";

export let DomainConceptPropertyComponent = {
    templateUrl: 'app/sensors/components/property/domain-concept-property.tmpl.html',
    bindings: {
        disabled : "<",
        domainProperty : "<"
    },
    controller: DomainConceptPropertyController,
    controllerAs: 'ctrl'
};