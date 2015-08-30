angular
    .module('pipelines', ['ngMaterial','ngMdIcons'])
    .controller('PipelineCtrl', [ '$scope','restApi','$http','$rootScope', function ($scope, restApi, $http, $rootScope) {
        $scope.pipelines = [];


        $scope.getPipelines = function(){
            restApi.getOwnPipelines()
                .success(function(pipelines){
                    $scope.pipelines = pipelines;
                    console.log($scope.pipelines);
                })
                .error(function(msg){
                    console.log(msg);
                });

        };
        $scope.getPipelines();


        $scope.startPipeline = function(pipelineId) {
        	restApi.startPipeline(pipelineId).success(function(data) {
        		console.log("starting pipeline");
        	});
        };
        
        $scope.stopPipeline = function(pipelineId) {
        	restApi.stopPipeline(pipelineId).success(function(data) {
        		console.log("stopping pipeline");
        	});
        };

        //$(refreshPipelines());

        //Bind click handler--------------------------------
        //$("#pipelineTableBody").on("click", "tr", function () {
        //    if (!$(this).data("active") || $(this).data("active") == undefined) {
        //        $(this).data("active", true);
        //        $(this).addClass("info");
        //        $("#pipelineTableBody").children().not(this).removeClass("info");
        //        $("#pipelineTableBody").children().not(this).data("active", false);
        //        clearPipelineDisplay();
        //        displayPipeline($(this).data("JSON"));
        //    } else {
        //
        //    }
        //});
    }]);