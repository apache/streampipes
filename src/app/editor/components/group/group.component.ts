import {GroupController} from "./group.controller";
declare const require: any;

export let GroupComponent = {
    template: require('./group.tmpl.html'),
    bindings: {
        staticProperty: "=",
        inputStreams : "=",
        selectedElement: "=",
        customizeForm: "="
    },
    controller: GroupController,
    controllerAs: 'ctrl'

};
