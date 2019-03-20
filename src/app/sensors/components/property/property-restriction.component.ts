import {PropertyRestrictionController} from "./property-restriction.controller";
declare const require: any;

export let PropertyRestrictionComponent = {
    template: require('./property-restriction.tmpl.html'),
    bindings: {
        disabled : "<",
        restriction : "=element"
    },
    controller: PropertyRestrictionController,
    controllerAs: 'ctrl'
};