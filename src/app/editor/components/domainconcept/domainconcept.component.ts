import {DomainConceptController} from "./domainconcept.controller";

export let DomainConceptComponent = {
    templateUrl: 'domainconcept.tmpl.html',
    bindings: {
        staticProperty : "=",
        autoCompleteStaticProperty : "="
    },
    controller: DomainConceptController,
    controllerAs: 'ctrl'
};
