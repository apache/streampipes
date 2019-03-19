import {DomainConceptPropertyController} from "./domain-concept-property.controller";
declare const require: any;

export let DomainConceptPropertyComponent = {
    template: require('./domain-concept-property.tmpl.html'),
    bindings: {
        disabled : "<",
        domainProperty : "="
    },
    controller: DomainConceptPropertyController,
    controllerAs: 'ctrl'
};