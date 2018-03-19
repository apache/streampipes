export class JsonLdDialogController {

    $mdDialog: any;
    title: any;
    content: any;

    constructor($mdDialog, title, content) {
        this.$mdDialog = $mdDialog;
        this.title = title;
        this.content = content;
    }

    hide() {
        this.$mdDialog.hide();
    };
    cancel() {
        this.$mdDialog.cancel();
    };
}

JsonLdDialogController.$inject = ['$mdDialog'];