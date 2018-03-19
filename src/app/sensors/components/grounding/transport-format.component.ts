import {TransportFormatController} from "./transport-format.controller";

export let TransportFormatComponent = {
    templateUrl: 'transport-format.tmpl.html',
    bindings: {
        disabled : "<",
        grounding : "="
    },
    controller: TransportFormatController,
    controllerAs: 'ctrl'
};