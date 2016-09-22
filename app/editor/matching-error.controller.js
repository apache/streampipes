MatchingErrorController.$inject = ['$scope', '$rootScope', '$mdDialog', 'elementData'];

export default function MatchingErrorController($scope, $rootScope, $mdDialog, elementData) {
    $scope.msg = elementData;
    console.log(elementData);
    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}