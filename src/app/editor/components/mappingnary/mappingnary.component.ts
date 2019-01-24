import {MappingNaryController} from "./mappingnary.controller";

export let MappingNaryComponent = {
    templateUrl: 'mappingnary.tmpl.html',
    bindings: {
        staticProperty : "=",
        displayRecommended: "=",
        selectedElement: "<"
    },
    controller: MappingNaryController,
    controllerAs: 'ctrl'
};
