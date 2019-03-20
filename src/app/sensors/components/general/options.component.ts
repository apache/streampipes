import {OptionsController} from "./options.controller";
declare const require: any;

export let OptionsComponent = {
    template: './options.tmpl.html',
    bindings: {
        disabled : "<",
        options : "<"
    },
    controller: OptionsController,
    controllerAs: 'ctrl'
};