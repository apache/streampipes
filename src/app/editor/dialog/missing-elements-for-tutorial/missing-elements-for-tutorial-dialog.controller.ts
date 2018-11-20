export class MissingElementsForTutorialDialogController {

    $mdDialog: any;
    missingElements: any;

    constructor($mdDialog, missingElements) {
        this.$mdDialog = $mdDialog;
        this.missingElements = missingElements;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

MissingElementsForTutorialDialogController.$inject = ['$mdDialog', 'pipelineElements'];