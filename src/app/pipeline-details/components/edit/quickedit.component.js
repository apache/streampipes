import {QuickEditController} from "./quickedit.controller";

export let QuickEditComponent = {
    templateUrl: 'app/pipeline-details/components/edit/quickedit.tmpl.html',
    bindings: {
        pipeline: "<",
        selectedElement: "<"
    },
    controller: QuickEditController,
    controllerAs: 'ctrl'
};
