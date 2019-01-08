import {PropertySelectionController} from "./property-selection.controller";

export let PropertySelectionComponent = {
    templateUrl: 'property-selection.tmpl.html',
    bindings: {
        outputStrategy: "=",
        eventProperty: "=",
        layer: "@",
        togglePropertyKey: "&"
    },
    controller: PropertySelectionController,
    controllerAs: 'ctrl'
};