import {OneOfRemoteController} from "./oneof-remote.controller";
declare const require: any;

export let OneOfRemoteComponent = {
    template: require('./oneof-remote.tmpl.html'),
    bindings: {
        staticProperty : "=",
        eventProperties : "=",
        staticProperties: "=",
        belongsTo: "="
    },
    controller: OneOfRemoteController,
    controllerAs: 'ctrl'
};
