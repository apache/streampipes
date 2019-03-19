import {MeasurementUnitController} from "./measurement-unit.controller";
declare const require: any;

export let MeasurementUnitComponent = {
    template: require('./measurement-unit.tmpl.html'),
    bindings: {
        disabled : "<",
        property : "="
    },
    controller: MeasurementUnitController,
    controllerAs: 'ctrl'
};