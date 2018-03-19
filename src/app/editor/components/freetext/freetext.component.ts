import {FreeTextController} from "./freetext.controller";

export let FreeTextComponent = {
    templateUrl: 'freetext.tmpl.html',
    bindings: {
        staticProperty: "=",
        inputStreams : "=",
        mappingProperty: "="
    },
    controller: FreeTextController,
    controllerAs: 'ctrl'
};
