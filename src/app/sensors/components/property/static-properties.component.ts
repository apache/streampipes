import {StaticPropertiesController} from "./static-properties.controller";

export let StaticPropertiesComponent = {
    templateUrl: 'static-properties.tmpl.html',
    bindings: {
        disabled : "<",
        staticProperties : "=",
        streams: "="
    },
    controller: StaticPropertiesController,
    controllerAs: 'ctrl'
};