PipelineDetailsCtrl.$inject = ['$scope', 'restApi', '$rootScope', '$stateParams', 'pipelinePositioningService'];

export default function PipelineDetailsCtrl($scope, restApi, $rootScope, $stateParams, pipelinePositioningService) {

    var currentPipeline = $stateParams.pipeline;
    $scope.pipeline = {};

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
