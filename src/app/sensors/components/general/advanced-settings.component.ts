import {AdvancedSettingsController} from "./advanced-settings.controller";
declare const require: any;

export let AdvancedSettingsComponent = {
    template: require('./advanced-settings.tmpl.html'),
    bindings: {
        disabled : "<"
    },
    transclude: true,
    controller: AdvancedSettingsController,
    controllerAs: 'ctrl'
};