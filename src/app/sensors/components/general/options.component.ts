import {OptionsController} from "./options.controller";

export let OptionsComponent = {
    templateUrl: 'options.tmpl.html',
    bindings: {
        disabled : "<",
        options : "<"
    },
    controller: OptionsController,
    controllerAs: 'ctrl'
};