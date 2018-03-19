import {TransportProtocolController} from "./transport-protocol.controller";

export let TransportProtocolComponent = {
    templateUrl: 'transport-protocol.tmpl.html',
    bindings: {
        disabled : "<",
        grounding : "="
    },
    controller: TransportProtocolController,
    controllerAs: 'ctrl'
};