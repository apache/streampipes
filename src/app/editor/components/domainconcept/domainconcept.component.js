import {DomainConceptController} from "./domainconcept.controller";

export let DomainConceptComponent = {
    templateUrl: 'app/editor/components/domainconcept/domainconcept.tmpl.html',
    bindings: {
        staticProperty : "=",
        autoCompleteStaticProperty : "="
    },
    controller: DomainConceptController,
    controllerAs: 'ctrl'
};
