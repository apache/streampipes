import {MappingUnaryController} from "./mappingunary.controller";

export let MappingUnaryComponent = {
    templateUrl: 'mappingunary.tmpl.html',
    bindings: {
        staticProperty : "=",
        displayRecommended: "=",
        selectedElement: "=",
    },
    controller: MappingUnaryController,
    controllerAs: 'ctrl'
};
