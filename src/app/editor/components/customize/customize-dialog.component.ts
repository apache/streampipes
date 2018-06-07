import {CustomizeDialogController} from "./customize-dialog.controller";

export let CustomizeDialogComponent = {
    templateUrl: 'customize-dialog.tmpl.html',
    bindings: {
        staticProperty: "=",
        selectedElement: "=",
        displayRecommended: "=",
        customizeForm: "="
    },
    controller: CustomizeDialogController,
    controllerAs: 'ctrl'
};
