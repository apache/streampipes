import {CustomOutputController} from "./customoutput.controller";

export let CustomOutputComponent = {
    templateUrl: 'app/editor/components/customoutput/customoutput.tmpl.html',
    bindings: {
        outputStrategy: "="
    },
    controller: CustomOutputController,
    controllerAs: 'ctrl'
};
