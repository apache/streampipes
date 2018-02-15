export class HelpDialogController {

    constructor($mdDialog, pipelineElement) {
        this.$mdDialog = $mdDialog;
        this.pipelineElement = pipelineElement;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

HelpDialogController.$inject = ['$mdDialog', 'pipelineElement'];