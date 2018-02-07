import {PropertyRestrictionController} from "./property-restriction.controller";

export let PropertyRestrictionComponent = {
    templateUrl: 'app/sensors/components/property/property-restriction.tmpl.html',
    bindings: {
        disabled : "<",
        restriction : "<"
    },
    controller: PropertyRestrictionController,
    controllerAs: 'ctrl'
};