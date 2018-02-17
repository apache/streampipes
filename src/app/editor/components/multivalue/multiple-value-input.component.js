import {MultipleValueInputController} from "./multiple-value-input.controller";

export let MultipleValueInputComponent = {
    templateUrl: 'app/editor/components/multivalue/multiple-value-input.tmpl.html',
    bindings: {
        staticProperty : "="
    },
    controller: MultipleValueInputController,
    controllerAs: 'ctrl'
};
