import {QuickEditController} from "./quickedit.controller";

export let QuickEditComponent = {
    templateUrl: 'quickedit.tmpl.html',
    bindings: {
        pipeline: "<",
        selectedElement: "<"
    },
    controller: QuickEditController,
    controllerAs: 'ctrl'
};
