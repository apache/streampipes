export class PipelineStatusDialogController {

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