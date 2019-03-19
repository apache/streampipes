import {SupportedGroundingController} from "./supported-grounding.controller";
declare const require: any;

export let SupportedGroundingComponent = {
    template: require('./supported-grounding.tmpl.html'),
    bindings: {
        disabled : "<",
        grounding : "="
    },
    controller: SupportedGroundingController,
    controllerAs: 'ctrl'
};