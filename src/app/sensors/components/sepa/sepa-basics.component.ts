import {SepaBasicsController} from "./sepa-basics.controller";
declare const require: any;

export let SepaBasicsComponent = {
    template: require('./sepa-basics.tmpl.html'),
    bindings: {
        disabled : "<",
        element : "="
    },
    controller: SepaBasicsController,
    controllerAs: 'ctrl'
};