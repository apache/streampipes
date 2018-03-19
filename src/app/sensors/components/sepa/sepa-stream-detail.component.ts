import {SepaStreamDetailController} from "./sepa-stream-detail.controller";

export let SepaStreamDetailComponent = {
    templateUrl: 'sepa-stream-detail.tmpl.html',
    bindings: {
        disabled : "<",
        stream : "="
    },
    controller: SepaStreamDetailController,
    controllerAs: 'ctrl'
};