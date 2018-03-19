import {MappingNaryController} from "./mappingnary.controller";

export let MappingNaryComponent = {
    templateUrl: 'mappingnary.tmpl.html',
    bindings: {
        staticProperty : "=",
        displayRecommended: "="
    },
    controller: MappingNaryController,
    controllerAs: 'ctrl'
};
