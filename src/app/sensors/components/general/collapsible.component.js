import {CollapsibleController} from "./collapsible.controller";

export let CollapsibleComponent = {
    templateUrl: 'app/sensors/components/general/collapsible.tmpl.html',
    bindings: {
        list : "<",
        ctr: "<",
        disabled: "<",
        removable: "<",
        titleLabel: "<",
        collapsible: "<",
        subtitle: "<"
    },
    transclude: true,
    controller: CollapsibleController,
    controllerAs: 'ctrl'
};