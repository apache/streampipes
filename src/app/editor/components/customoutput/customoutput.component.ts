import {CustomOutputController} from "./customoutput.controller";

export let CustomOutputComponent = {
    templateUrl: 'customoutput.tmpl.html',
    bindings: {
        outputStrategy: "=",
        selectedElement: "="
    },
    controller: CustomOutputController,
    controllerAs: 'ctrl'
};
