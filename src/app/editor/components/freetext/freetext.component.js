import {FreeTextController} from "./freetext.controller";

export let FreeTextComponent = {
    templateUrl: 'app/editor/components/freetext/freetext.tmpl.html',
    bindings: {
        staticProperty: "=",
        inputStreams : "=",
        mappingProperty: "="
    },
    controller: FreeTextController,
    controllerAs: 'ctrl'
};
