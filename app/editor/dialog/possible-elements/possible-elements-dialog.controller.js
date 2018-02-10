export class PossibleElementsController {

    constructor($scope, $mdDialog, ElementIconText) {
        this.$scope = $scope;
        this.$mdDialog = $mdDialog;
        this.ElementIconText = ElementIconText;
    }


    create(possibleElement) {
        this.$scope.createFunction(this.$scope.getPipelineElementContents(possibleElement.elementId), this.$scope.getDomElement($scope.internalId));
    }

    type(possibleElement) {
        return this.$scope.getPipelineElementContents(possibleElement.elementId).type;
    }

    iconText(elementId) {
        return this.ElementIconText.getElementIconText(elementId);
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

PossibleElementsController.$inject = ['$scope', '$mdDialog', 'ElementIconText'];