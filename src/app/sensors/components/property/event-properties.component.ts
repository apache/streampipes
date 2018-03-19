import {EventPropertiesController} from "./event-properties.controller";

export let EventPropertiesComponent = {
    templateUrl: 'event-properties.tmpl.html',
    bindings: {
        disabled : "<",
        properties : "="
    },
    controller: EventPropertiesController,
    controllerAs: 'ctrl'
};