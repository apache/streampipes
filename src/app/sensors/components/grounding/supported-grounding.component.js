import {SupportedGroundingController} from "./supported-grounding.controller";

export let SupportedGroundingComponent = {
    templateUrl: 'app/sensors/components/grounding/supported-grounding.tmpl.html',
    bindings: {
        disabled : "<",
        grounding : "="
    },
    controller: SupportedGroundingController,
    controllerAs: 'ctrl'
};