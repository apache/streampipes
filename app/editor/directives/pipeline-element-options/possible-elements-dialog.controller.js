PossibleElementsController.$inject = ['$scope', '$mdDialog', 'getElementIconText'];

export default function PossibleElementsController($scope, $mdDialog, getElementIconText) {


    $scope.create = function(possibleElement) {
        $scope.createFunction($scope.getPipelineElementContents(possibleElement.elementId), $scope.getDomElement($scope.internalId));
    }

    $scope.type = function(possibleElement) {
        return $scope.getPipelineElementContents(possibleElement.elementId).type;
    }

    $scope.iconText = function(elementId) {
        return getElementIconText(elementId);
    }

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}