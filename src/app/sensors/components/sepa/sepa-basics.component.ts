import {SepaBasicsController} from "./sepa-basics.controller";

export let SepaBasicsComponent = {
    templateUrl: 'sepa-basics.tmpl.html',
    bindings: {
        disabled : "<",
        element : "="
    },
    controller: SepaBasicsController,
    controllerAs: 'ctrl'
};