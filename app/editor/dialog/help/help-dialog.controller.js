export class HelpDialogController {

    constructor($scope, $mdDialog, pipelineElement) {
        this.$scope = $scope;
        this.$mdDialog = $mdDialog;
        this.pipelineElement = pipelineElement;
        this.$scope.pipelineElement = pipelineElement;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

HelpDialogController.$inject = ['$scope', '$mdDialog', 'pipelineElement'];