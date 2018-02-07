import {StreamRestrictionController} from "./stream-restriction.controller";

export let StreamRestrictionComponent = {
    templateUrl: 'app/sensors/components/restriction/stream-restriction.tmpl.html',
    bindings: {
        disabled : "<",
        streams : "=element"
    },
    controller: StreamRestrictionController,
    controllerAs: 'ctrl'
};