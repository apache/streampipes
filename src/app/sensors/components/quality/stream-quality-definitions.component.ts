import {StreamQualityDefinitionsController} from "./stream-quality-definitions.controller";

export let StreamQualityDefinitionsComponent = {
    templateUrl: 'stream-quality-definitions.tmpl.html',
    bindings: {
        disabled : "<",
        property : "=element",
        runtimeType: "="
    },
    controller: StreamQualityDefinitionsController,
    controllerAs: 'ctrl'
};