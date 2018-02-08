HelpDialogController.$inject = ['$scope', '$mdDialog', 'pipelineElement'];

export default function HelpDialogController($scope, $mdDialog, pipelineElement) {

    $scope.pipelineElement = pipelineElement;
    console.log(pipelineElement);
    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}