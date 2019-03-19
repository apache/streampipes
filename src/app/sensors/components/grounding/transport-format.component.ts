import {TransportFormatController} from "./transport-format.controller";
declare const require: any;

export let TransportFormatComponent = {
    template: require('./transport-format.tmpl.html'),
    bindings: {
        disabled : "<",
        grounding : "="
    },
    controller: TransportFormatController,
    controllerAs: 'ctrl'
};