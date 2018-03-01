export class JsonLdDialogController {

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

JsonLdDialogController.$inject = ['$mdDialog'];