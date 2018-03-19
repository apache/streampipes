import {MeasurementUnitController} from "./measurement-unit.controller";

export let MeasurementUnitComponent = {
    templateUrl: 'measurement-unit.tmpl.html',
    bindings: {
        disabled : "<",
        property : "="
    },
    controller: MeasurementUnitController,
    controllerAs: 'ctrl'
};