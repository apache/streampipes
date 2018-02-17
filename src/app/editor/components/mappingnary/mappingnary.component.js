import {MappingNaryController} from "./mappingnary.controller";

export let MappingNaryComponent = {
    templateUrl: 'app/editor/components/mappingnary/mappingnary.tmpl.html',
    bindings: {
        staticProperty : "=",
        displayRecommended: "="
    },
    controller: MappingNaryController,
    controllerAs: 'ctrl'
};
