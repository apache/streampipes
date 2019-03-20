import {MappingUnaryController} from "./mappingunary.controller";
declare const require: any;

export let MappingUnaryComponent = {
    template: require('./mappingunary.tmpl.html'),
    bindings: {
        staticProperty : "=",
        displayRecommended: "=",
        selectedElement: "=",
    },
    controller: MappingUnaryController,
    controllerAs: 'ctrl'
};
