import {DomainPropertyController} from "./domain-property.controller";
declare const require: any;

export let DomainPropertyComponent = {
    template: require('./domain-property.tmpl.html'),
    bindings: {
        disabled : "<",
        property : "="
    },
    controller: DomainPropertyController,
    controllerAs: 'ctrl'
};