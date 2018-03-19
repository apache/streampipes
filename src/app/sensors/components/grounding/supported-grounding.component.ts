import {SupportedGroundingController} from "./supported-grounding.controller";

export let SupportedGroundingComponent = {
    templateUrl: 'supported-grounding.tmpl.html',
    bindings: {
        disabled : "<",
        grounding : "="
    },
    controller: SupportedGroundingController,
    controllerAs: 'ctrl'
};