export class MatchingErrorController {

    $mdDialog: any;
    elementData: any;
    msg: any;

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