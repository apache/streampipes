import {StaticPropertiesController} from "./static-properties.controller";
declare const require: any;

export let StaticPropertiesComponent = {
    template: require('./static-properties.tmpl.html'),
    bindings: {
        disabled : "<",
        staticProperties : "=",
        streams: "="
    },
    controller: StaticPropertiesController,
    controllerAs: 'ctrl'
};