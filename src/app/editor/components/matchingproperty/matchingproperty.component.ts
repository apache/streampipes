import {MatchingPropertyController} from "./matchingproperty.controller";
declare const require: any;

export let MatchingPropertyComponent = {
    template: require('./matchingproperty.tmpl.html'),
    bindings: {
        staticProperty : "="
    },
    controller: MatchingPropertyController,
    controllerAs: 'ctrl'
};
