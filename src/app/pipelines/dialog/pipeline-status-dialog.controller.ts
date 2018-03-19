export class PipelineStatusDialogController {

    $mdDialog: any;

    constructor($mdDialog) {
        this.$mdDialog = $mdDialog;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

PipelineStatusDialogController.$inject = ['$mdDialog'];