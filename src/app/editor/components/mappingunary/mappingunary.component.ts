import {MappingUnaryController} from "./mappingunary.controller";

export let MappingUnaryComponent = {
    templateUrl: 'mappingunary.tmpl.html',
    bindings: {
        staticProperty : "=",
        displayRecommended: "="
    },
    controller: MappingUnaryController,
    controllerAs: 'ctrl'
};
