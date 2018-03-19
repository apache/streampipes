import {StreamRestrictionController} from "./stream-restriction.controller";

export let StreamRestrictionComponent = {
    templateUrl: 'stream-restriction.tmpl.html',
    bindings: {
        disabled : "<",
        streams : "=element"
    },
    controller: StreamRestrictionController,
    controllerAs: 'ctrl'
};