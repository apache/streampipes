import {SepaStreamDetailController} from "./sepa-stream-detail.controller";
declare const require: any;

export let SepaStreamDetailComponent = {
    template: require('./sepa-stream-detail.tmpl.html'),
    bindings: {
        disabled : "<",
        stream : "="
    },
    controller: SepaStreamDetailController,
    controllerAs: 'ctrl'
};