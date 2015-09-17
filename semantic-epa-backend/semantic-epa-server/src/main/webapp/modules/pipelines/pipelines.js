angular
    .module('streamPipesApp')
    .controller('PipelineCtrl', [ '$scope','restApi','$http','$rootScope','$mdDialog','$location','apiConstants', '$state', function ($scope, restApi, $http, $rootScope, $mdDialog, $location, apiConstants, $state) {
        $scope.pipeline = {};
        $scope.pipelines = [];
        $scope.pipelinShowing = false;
        var pipelinePlumb = jsPlumb.getInstance({Container: "pipelineDisplay"});
        $scope.starting = false;
        $scope.stopping = false;

        $scope.isStarting = function(){
            return $scope.starting;
        }

        $scope.$on('$destroy', function () {
            pipelinePlumb.deleteEveryEndpoint();
        });

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

        $scope.showDialog = function(data){
        	 $mdDialog.show({
        	      controller: PipelineStatusDialogController,
        	      templateUrl: 'modules/pipelines/templates/pipelineOperationDialog.tmpl.html',
        	      parent: angular.element(document.body),
        	      clickOutsideToClose:true,
        	      locals : {
                      data : data
                  }
        	    })
        };

        $scope.startPipeline = function(pipelineId) {
            $scope.starting = true;
            console.log("starting pipeline");
        	restApi.startPipeline(pipelineId)
                .success(function(data) {
                    $scope.starting = false;
                    console.log(data);
                    $scope.showDialog(data);
                    $scope.getPipelines();
        	    })
                .error(function(data){
                    console.log(data);
                    $scope.starting = false;
                    $scope.showDialog({notifications : [{title : "Network Error", description : "Please check your Network."}]});

                });
        };
        
        $scope.stopPipeline = function(pipelineId) {
            console.log("stopping pipeline");
            $scope.stopping = true;
        	restApi.stopPipeline(pipelineId)
                .success(function(data) {
                    $scope.stopping = false;
                    $scope.showDialog(data);
                    $scope.getPipelines();
        	    })
                .error(function(data){
                    console.log(data);
                    $scope.stopping = false;
                    $scope.showDialog({notifications : [{title : "Network Error", description : "Please check your Network."}]});

                });
        };

        $scope.deletePipeline = function(pipelineId) {
            restApi.deleteOwnPipeline(pipelineId)
                .success(function(data){
                    console.log(data);
                })
                .error(function(data){
                    console.log(data);
                })
        };

        $scope.showPipeline = function(pipeline){

            clearPipelineDisplay();
            displayPipelineById(pipeline);
        };
        $scope.modifyPipeline = function(pipeline){
            showPipelineInEditor(pipeline);

        };

        function displayPipelineById(json){

            console.log("displayPipeline()");
            for (var i = 0, stream; stream = json.streams[i]; i++){
                createPreviewElement("stream", stream, i, json);
            }
            for (var i = 0, sepa; sepa = json.sepas[i]; i++){

                createPreviewElement("sepa", sepa, i, json);
            }
            createPreviewElement("action", json.action);
            connectPipelineElements(json, false);
            pipelinePlumb.repaintEverything(true);
        }
        function createPreviewElement(type, element, i, json){

            var $state = $("<span>")
                .addClass("connectable a")
                .attr("id", element.DOM)
                .data("JSON", $.extend(true, {}, element));
            if (element.iconUrl == null){ //Kein icon in JSON angegeben
                addTextIconToElement($state, $state.data("JSON").name);

                //.data("JSON", $.extend(true, {},element));
            }else{
                $('<img>')
                    .addClass('connectable-img tt')
                    .attr(
                    {
                        src : element.iconUrl,
                        "data-toggle": "tooltip",
                        "data-placement": "top",
                        "data-delay": '{"show": 1000, "hide": 100}',
                        title: element.name
                    })
                    .error(function(){
                        addTextIconToElement($state, $state.data("JSON").name);
                        $(this).remove();
                    })
                    .appendTo($state)

                    .data("JSON", $.extend(true, {},element));
            }

            var topLeftY, topLeftX;

            switch (type){

                case "stream":
                    $state.appendTo("body");
                    $state.addClass("stream");
                    topLeftY = getYPosition(json.streams.length , i, $("#streamDisplay"), $state);
                    topLeftX = getXPosition($("#streamDisplay"), $state);
                    $state.appendTo("#streamDisplay");
                    break;

                // jsPlumb.addEndpoint($icon,streamEndpointOptions);
                case "sepa":
                    $state.appendTo("body");
                    $state.addClass("sepa");
                    topLeftY = getYPosition(json.sepas.length , i, $("#sepaDisplay"), $state);
                    topLeftX = getXPosition($("#sepaDisplay"), $state);
                    $state.appendTo("#sepaDisplay");
                    break;

                case "action":
                    $state.appendTo("body");
                    $state.addClass("action");
                    topLeftY = $("#actionDisplay").height() / 2 - (1/2) * $state.outerHeight();
                    topLeftX = $("#actionDisplay").width() / 2 - (1/2) * $state.outerWidth();
                    $state.appendTo("#actionDisplay");
                    break;
            }
            $state.css(
                {
                    "position" : "absolute",
                    "top": topLeftY,
                    "left": topLeftX
                }
            );
        }

        function connectPipelineElements(json, detachable){
            console.log("connectPipelineElements()");
            var source, target;

            pipelinePlumb.setSuspendDrawing(true);
            if (!$.isEmptyObject(json.action)) {
                //Action --> Sepas----------------------//
                target = json.action.DOM;

                for (var i = 0, connection; connection = json.action.connectedTo[i]; i++) {
                    source = connection;

                    var sourceEndpoint = pipelinePlumb.addEndpoint(source, apiConstants.sepaEndpointOptions);
                    var targetEndpoint = pipelinePlumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
                    pipelinePlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
                }
            }
            //Sepas --> Streams / Sepas --> Sepas---------------------//
            for (var i = 0, sepa; sepa = json.sepas[i]; i++){
                for (var j = 0, connection; connection = sepa.connectedTo[j]; j++){

                    source = connection;
                    target = sepa.DOM;


                    var options;
                    var id = "#" + source;
                    console.log($(id));
                    if ($(id).hasClass("sepa")){
                        options = apiConstants.sepaEndpointOptions;
                    }else{
                        options = apiConstants.streamEndpointOptions;
                    }

                    var sourceEndpoint = pipelinePlumb.addEndpoint(source, options);
                    var targetEndpoint = pipelinePlumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
                    pipelinePlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable:detachable});
                }
            }
            pipelinePlumb.setSuspendDrawing(false ,true);
        }

        function clearPipelineDisplay(){
            pipelinePlumb.deleteEveryEndpoint();
            $("#pipelineDisplay").children().each(function(){
                $(this).children().remove();
            });
        }

        function showPipelineInEditor(id){
        	$state.go("streampipes.edit", {pipeline : id});
        }

        function PipelineDialogController($scope, $mdDialog, pipeline){
            $scope.pipeline = pipeline;

            $scope.hide = function(){
                $mdDialog.hide();
            };
            $timeout(displayPipelineById($scope.pipeline));
        }
        
        function PipelineStatusDialogController($scope, $mdDialog, data) {
        	
        	$scope.data = data;
        	
      	  $scope.hide = function() {
      	    $mdDialog.hide();
      	  };
      	  $scope.cancel = function() {
      	    $mdDialog.cancel();
      	  };
      	}

        //$(refreshPipelines());

        //Bind click handler--------------------------------
        //$("#pipelineTableBody").on("click", "tr", function () {
        //    if (!$(this).data("active") || $(this).data("active") == undefined) {
        //        $(this).data("active", true);
        //        $(this).addClass("info");
        //        $("#pipelineTableBody").children().not(this).removeClass("info");
        //        $("#pipelineTableBody").children().not(this).data("active", false);
        //        clearPipelineDisplay();
        //        displayPipelineById($(this).data("JSON"));
        //    } else {
        //
        //    }
        //});
    }]);

