export class JsonLdDialogController {

    $mdDialog: any;
    title: any;
    content: any;
    loadingCompleted: any;

    constructor($mdDialog, $timeout, title, content) {
        this.$mdDialog = $mdDialog;
        this.title = title;
        this.content = content;
        $timeout(() => {
            this.loadingCompleted = true;
        });
    }

    hide() {
        this.$mdDialog.hide();
    };
    cancel() {
        this.$mdDialog.cancel();
    };
}

JsonLdDialogController.$inject = ['$mdDialog', '$timeout', 'title', 'content'];