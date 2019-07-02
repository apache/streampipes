import {OneOfRemoteController} from "./oneof-remote.controller";
declare const require: any;

export let OneOfRemoteComponent = {
    template: require('./oneof-remote.tmpl.html'),
    bindings: {
        staticProperty : "=",
        selectedElement: "="
    },
    controller: OneOfRemoteController,
    controllerAs: 'ctrl'
};
