import {AlternativeController} from "./alternative.controller";
declare const require: any;

export let AlternativeComponent = {
    template: require('./alternative.tmpl.html'),
    bindings: {
        staticProperty: "=",
        selectedElement: "=",
        displayRecommended: "=",
        customizeForm: "="
    },
    controller: AlternativeController,
    controllerAs: 'ctrl'
};
