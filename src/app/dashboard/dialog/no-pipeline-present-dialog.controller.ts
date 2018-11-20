export class NoPipelinePresentDialogController {

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

NoPipelinePresentDialogController.$inject = ['$mdDialog'];