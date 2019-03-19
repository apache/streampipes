import {EventPropertiesController} from "./event-properties.controller";
declare const require: any;

export let EventPropertiesComponent = {
    template: require('./event-properties.tmpl.html'),
    bindings: {
        disabled : "<",
        properties : "="
    },
    controller: EventPropertiesController,
    controllerAs: 'ctrl'
};