import {PropertyQualityDefinitionsController} from "./property-quality-definitions.controller";
declare const require: any;

export let PropertyQualityDefinitionsComponent = {
    template: require('./property-quality-definitions.tmpl.html'),
    bindings: {
        disabled : "<",
        property : "=",
        runtimeType: "="
    },
    controller: PropertyQualityDefinitionsController,
    controllerAs: 'ctrl'
};