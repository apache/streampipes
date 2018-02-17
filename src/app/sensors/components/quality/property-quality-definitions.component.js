import {PropertyQualityDefinitionsController} from "./property-quality-definitions.controller";

export let PropertyQualityDefinitionsComponent = {
    templateUrl: 'app/sensors/components/quality/property-quality-definitions.tmpl.html',
    bindings: {
        disabled : "<",
        property : "=",
        runtimeType: "="
    },
    controller: PropertyQualityDefinitionsController,
    controllerAs: 'ctrl'
};