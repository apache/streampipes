import {MappingNaryController} from "./mappingnary.controller";
declare const require: any;

export let MappingNaryComponent = {
    template: require('./mappingnary.tmpl.html'),
    bindings: {
        staticProperty : "=",
        displayRecommended: "=",
        selectedElement: "<"
    },
    controller: MappingNaryController,
    controllerAs: 'ctrl'
};
