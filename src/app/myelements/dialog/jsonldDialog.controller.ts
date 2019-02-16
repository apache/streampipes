export class JsonLdDialogController {

    $mdDialog: any;
    $timeout: any;
    title: any;
    content: any;
    loadingCompleted: any;

    constructor($mdDialog, $timeout, title, content) {
        this.$mdDialog = $mdDialog;
        this.$timeout = $timeout;
        this.title = title;
        this.content = content;
    }

    $onInit() {
        this.$timeout(() => {
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