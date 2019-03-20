import {TransportProtocolController} from "./transport-protocol.controller";

export let TransportProtocolComponent = {
    templateUrl: '/src/app/sensors/components/grounding/transport-protocol.tmpl.html',
    bindings: {
        disabled : "<",
        grounding : "="
    },
    controller: TransportProtocolController,
    controllerAs: 'ctrl'
};