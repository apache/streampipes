import {DomainConceptController} from "./domainconcept.controller";
declare const require: any;

export let DomainConceptComponent = {
    template: require('./domainconcept.tmpl.html'),
    bindings: {
        staticProperty : "=",
        autoCompleteStaticProperty : "="
    },
    controller: DomainConceptController,
    controllerAs: 'ctrl'
};
