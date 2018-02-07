import {OptionsController} from "./options.controller";

export let OptionsComponent = {
    templateUrl: 'app/sensors/components/general/options.tmpl.html',
    bindings: {
        disabled : "<",
        options : "<"
    },
    controller: OptionsController,
    controllerAs: 'ctrl'
};