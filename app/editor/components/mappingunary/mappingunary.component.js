import {MappingUnaryController} from "./mappingunary.controller";

export let MappingUnaryComponent = {
    templateUrl: 'app/editor/components/mappingunary/mappingunary.tmpl.html',
    bindings: {
        staticProperty : "=",
        displayRecommended: "="
    },
    controller: MappingUnaryController,
    controllerAs: 'ctrl'
};
