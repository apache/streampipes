import {SepaBasicsController} from "./sepa-basics.controller";

export let SepaBasicsComponent = {
    templateUrl: 'app/sensors/components/sepa/sepa-basics.tmpl.html',
    bindings: {
        disabled : "<",
        element : "="
    },
    controller: SepaBasicsController,
    controllerAs: 'ctrl'
};