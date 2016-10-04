StopAllPipelinesController.$inject = ['$scope', '$mdDialog', 'pipelines'];

export default function StopAllPipelinesController($scope, $mdDialog, pipelines) {



    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}