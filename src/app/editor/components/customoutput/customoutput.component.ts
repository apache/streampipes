import {CustomOutputController} from "./customoutput.controller";
declare const require: any;

export let CustomOutputComponent = {
    template: require('./customoutput.tmpl.html'),
    bindings: {
        outputStrategy: "=",
        selectedElement: "="
    },
    controller: CustomOutputController,
    controllerAs: 'ctrl'
};
