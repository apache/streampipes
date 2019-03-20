import {ConfigItemController} from "./config-item.controller";

declare const require: any;

export let ConfigItemComponent = {
    template: require('./config-item.tmpl.html'),
    bindings: {
        elementTitle: "@",
    },
    controller: ConfigItemController,
    controllerAs: 'ctrl',
    transclude: true
};
