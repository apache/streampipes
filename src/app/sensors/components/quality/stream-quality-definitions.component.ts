import {StreamQualityDefinitionsController} from "./stream-quality-definitions.controller";
declare const require: any;

export let StreamQualityDefinitionsComponent = {
    template: require('./stream-quality-definitions.tmpl.html'),
    bindings: {
        disabled : "<",
        property : "=element",
        runtimeType: "="
    },
    controller: StreamQualityDefinitionsController,
    controllerAs: 'ctrl'
};