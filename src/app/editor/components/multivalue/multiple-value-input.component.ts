import {MultipleValueInputController} from "./multiple-value-input.controller";
declare const require: any;

export let MultipleValueInputComponent = {
    template: require('./multiple-value-input.tmpl.html'),
    bindings: {
        staticProperty : "="
    },
    controller: MultipleValueInputController,
    controllerAs: 'ctrl'
};
