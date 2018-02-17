export class MatchingErrorController {

    constructor($mdDialog, elementData) {
        this.$mdDialog = $mdDialog;
        this.elementData = elementData;

        this.msg = elementData;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

MatchingErrorController.$inject = ['$mdDialog', 'elementData'];