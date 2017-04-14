PipelineDetailsCtrl.$inject = ['$scope', 'restApi', '$rootScope', '$stateParams', 'pipelinePositioningService'];

export default function PipelineDetailsCtrl($scope, restApi, $rootScope, $stateParams, pipelinePositioningService) {

    var currentPipeline = $stateParams.pipeline;
    $scope.pipeline = {};

    var loadPipeline = function () {
        restApi.getPipelineById(currentPipeline)
            .success(function (pipeline) {
                $scope.pipeline = pipeline;
                showPipelinePreview(pipeline);

            })
            .error(function (msg) {
                console.log(msg);
            });
    }

    var showPipelinePreview = function(pipeline) {
        pipelinePositioningService.drawPipeline(null, pipeline);
    }

    loadPipeline();

}
