import {FreeTextController} from "./freetext.controller";
declare const require: any;

export let FreeTextComponent = {
    template: require('./freetext.tmpl.html'),
    bindings: {
        staticProperty: "=",
        inputStreams : "=",
        mappingProperty: "=",
        customizeForm: "="
    },
    controller: FreeTextController,
    controllerAs: 'ctrl'
};
