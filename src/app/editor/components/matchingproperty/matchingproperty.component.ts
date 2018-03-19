import {MatchingPropertyController} from "./matchingproperty.controller";

export let MatchingPropertyComponent = {
    templateUrl: 'matchingproperty.tmpl.html',
    bindings: {
        staticProperty : "="
    },
    controller: MatchingPropertyController,
    controllerAs: 'ctrl'
};
