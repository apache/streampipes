StartAllPipelinesController.$inject = ['$scope', '$mdDialog', 'pipelines'];

export default function StartAllPipelinesController($scope, $mdDialog, pipelines) {

        

    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}