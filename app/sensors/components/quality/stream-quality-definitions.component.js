import {StreamQualityDefinitionsController} from "./stream-quality-definitions.controller";

export let StreamQualityDefinitionsComponent = {
    templateUrl: 'app/sensors/components/quality/stream-quality-definitions.tmpl.html',
    bindings: {
        disabled : "<",
        property : "<",
        runtimeType: "<"
    },
    controller: StreamQualityDefinitionsController,
    controllerAs: 'ctrl'
};