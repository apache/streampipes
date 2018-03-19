import {PropertyRestrictionController} from "./property-restriction.controller";

export let PropertyRestrictionComponent = {
    templateUrl: 'property-restriction.tmpl.html',
    bindings: {
        disabled : "<",
        restriction : "=element"
    },
    controller: PropertyRestrictionController,
    controllerAs: 'ctrl'
};