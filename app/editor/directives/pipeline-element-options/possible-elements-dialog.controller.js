PossibleElementsController.$inject = ['$scope', '$mdDialog', 'ElementIconText'];

export default function PossibleElementsController($scope, $mdDialog, ElementIconText) {


    $scope.create = function(possibleElement) {
        $scope.createFunction($scope.getPipelineElementContents(possibleElement.elementId), $scope.getDomElement($scope.internalId));
    }

    $scope.type = function(possibleElement) {
        return $scope.getPipelineElementContents(possibleElement.elementId).type;
    }

    $scope.iconText = function(elementId) {
        return ElementIconText.getElementIconText(elementId);
    }

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}