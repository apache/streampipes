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