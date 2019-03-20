import {QuickEditController} from "./quickedit.controller";
declare const require: any;

export let QuickEditComponent = {
    template: require('./quickedit.tmpl.html'),
    bindings: {
        pipeline: "<",
        selectedElement: "<"
    },
    controller: QuickEditController,
    controllerAs: 'ctrl'
};
