export class PipelineStatusDialogController {

    $mdDialog: any;
    statusDetailsVisible: any;

    constructor($mdDialog) {
        this.$mdDialog = $mdDialog;
        this.statusDetailsVisible = false;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };

    toggleStatusDetailsVisible() {
        this.statusDetailsVisible = !(this.statusDetailsVisible);
    }
}

PipelineStatusDialogController.$inject = ['$mdDialog'];