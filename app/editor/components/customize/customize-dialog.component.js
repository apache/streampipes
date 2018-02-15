import {CustomizeDialogController} from "./customize-dialog.controller";

export let CustomizeDialogComponent = {
    templateUrl: 'app/editor/components/customize/customize-dialog.tmpl.html',
    bindings: {
        staticProperty: "=",
        selectedElement: "=",
        displayRecommended: "="
    },
    controller: CustomizeDialogController,
    controllerAs: 'ctrl'
};
