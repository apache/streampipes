import {CollapsibleController} from "./collapsible.controller";
declare const require: any;

export let CollapsibleComponent = {
    template: require('./collapsible.tmpl.html'),
    bindings: {
        list : "<",
        ctr: "<",
        disabled: "<",
        removable: "<",
        titleLabel: "<",
        collapsible: "<",
        subtitle: "<"
    },
    transclude: true,
    controller: CollapsibleController,
    controllerAs: 'ctrl'
};