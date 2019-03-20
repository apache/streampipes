import {CustomizeDialogController} from "./customize-dialog.controller";
declare const require: any;

export let CustomizeDialogComponent = {
    template: require('./customize-dialog.tmpl.html'),
    bindings: {
        staticProperty: "=",
        selectedElement: "=",
        displayRecommended: "=",
        customizeForm: "="
    },
    controller: CustomizeDialogController,
    controllerAs: 'ctrl'
};
