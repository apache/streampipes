PipelineDetailsCtrl.$inject = ['$scope', 'restApi', '$rootScope', '$stateParams', 'pipelinePositioningService'];

export default function PipelineDetailsCtrl($scope, restApi, $rootScope, $stateParams, pipelinePositioningService) {

    var currentPipeline = $stateParams.pipeline;
    $scope.pipeline = {};

    $scope.selectedTab = "overview";
    $scope.selectedElement = "";

    $scope.setSelectedTab = function(tabTitle) {
        $scope.selectedTab = tabTitle;
    }

    $scope.updateSelected = function(selected) {
        $scope.selectedElement = selected;
        $scope.$apply();
    }

    var loadPipeline = function () {
        restApi.getPipelineById(currentPipeline)
            .success(function (pipeline) {
                $scope.pipeline = pipeline;
            })
            .error(function (msg) {
                console.log(msg);
            });
    }

    loadPipeline();

}
