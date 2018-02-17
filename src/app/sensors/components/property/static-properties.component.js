import {StaticPropertiesController} from "./static-properties.controller";

export let StaticPropertiesComponent = {
    templateUrl: 'app/sensors/components/property/static-properties.tmpl.html',
    bindings: {
        disabled : "<",
        staticProperties : "=",
        streams: "="
    },
    controller: StaticPropertiesController,
    controllerAs: 'ctrl'
};