import {CustomOutputController} from "./customoutput.controller";

export let CustomOutputComponent = {
    templateUrl: 'customoutput.tmpl.html',
    bindings: {
        outputStrategy: "="
    },
    controller: CustomOutputController,
    controllerAs: 'ctrl'
};
