import {EventPropertiesController} from "./event-properties.controller";

export let EventPropertiesComponent = {
    templateUrl: 'app/sensors/components/property/event-properties.tmpl.html',
    bindings: {
        disabled : "<",
        properties : "<"
    },
    controller: EventPropertiesController,
    controllerAs: 'ctrl'
};