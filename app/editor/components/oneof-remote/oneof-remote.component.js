import {OneOfRemoteController} from "./oneof-remote.controller";

export let OneOfRemoteComponent = {
    templateUrl: 'app/editor/components/oneof-remote/oneof-remote.tmpl.html',
    bindings: {
        staticProperty : "=",
        eventProperties : "=",
        staticProperties: "=",
        belongsTo: "="
    },
    controller: OneOfRemoteController,
    controllerAs: 'ctrl'
};
