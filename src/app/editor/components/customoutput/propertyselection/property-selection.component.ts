import {PropertySelectionController} from "./property-selection.controller";
declare const require: any;

export let PropertySelectionComponent = {
    template: require('./property-selection.tmpl.html'),
    bindings: {
        outputStrategy: "=",
        eventProperty: "=",
        layer: "@",
        togglePropertyKey: "&"
    },
    controller: PropertySelectionController,
    controllerAs: 'ctrl'
};