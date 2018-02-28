export class JsonLdDialogController {

    constructor($mdDialog, title, content) {
        this.title = title;
        this.content = content;
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