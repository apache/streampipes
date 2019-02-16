export class MatchingErrorController {

    $mdDialog: any;
    elementData: any;
    msg: any;
    statusDetailsVisible: any;

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

    toggleStatusDetailsVisible() {
        this.statusDetailsVisible = !(this.statusDetailsVisible);
    }
}

MatchingErrorController.$inject = ['$mdDialog', 'elementData'];