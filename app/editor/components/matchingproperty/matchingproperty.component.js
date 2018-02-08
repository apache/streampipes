import {MatchingPropertyController} from "./matchingproperty.controller";

export let MatchingPropertyComponent = {
    templateUrl: 'app/editor/components/matchingproperty/matchingproperty.tmpl.html',
    bindings: {
        staticProperty : "="
    },
    controller: MatchingPropertyController,
    controllerAs: 'ctrl'
};
