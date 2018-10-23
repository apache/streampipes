import {ConfigItemController} from "./config-item.controller";

export let ConfigItemComponent = {
    templateUrl: 'config-item.tmpl.html',
    bindings: {
        elementTitle: "@",
    },
    controller: ConfigItemController,
    controllerAs: 'ctrl',
    transclude: true
};
