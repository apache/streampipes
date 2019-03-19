import {StreamRestrictionController} from "./stream-restriction.controller";
declare const require: any;

export let StreamRestrictionComponent = {
    template: require('./stream-restriction.tmpl.html'),
    bindings: {
        disabled : "<",
        streams : "=element"
    },
    controller: StreamRestrictionController,
    controllerAs: 'ctrl'
};