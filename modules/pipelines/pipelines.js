angular
    .module('streamPipesApp')
    .controller('PipelineCtrl', [ '$scope','restApi','$http','$rootScope','$mdDialog','$location','apiConstants', '$state','$timeout', '$stateParams', 'imageChecker',
        function ($scope, restApi, $http, $rootScope, $mdDialog, $location, apiConstants, $state, $timeout, $stateParams, imageChecker) {
        $scope.pipeline = {};
        $scope.pipelines = [];
        $scope.pipelinShowing = false;
        var pipelinePlumb = jsPlumb.getInstance({Container: "pipelineDisplay"});
        $scope.starting = false;
        $scope.stopping = false;
        
        $scope.pipelineStatus = [];
        $scope.pipelineCategories = [];
        $scope.activeCategory = "";
        
        $scope.startPipelineDirectly = $stateParams.pipeline;
                
        var textInputFields = [];

            (function init(){
                $(document).on("click", function(){
                    $("#contextMenu").hide();
                });
            })();

        $scope.$on('$destroy', function () {
            pipelinePlumb.deleteEveryEndpoint();
        });
        
        $scope.setSelectedTab = function(categoryId) {
        	$scope.activeCategory = categoryId;
        }

        $scope.getPipelines = function(){
            restApi.getOwnPipelines()
                .success(function(pipelines){
                    $scope.pipelines = pipelines;
                    $timeout(function(){
                        addContextMenu();
                    })
                })
                .error(function(msg){
                    console.log(msg);
                });

        };
        $scope.getPipelines();
        
        $scope.getPipelineCategories = function(){
            restApi.getPipelineCategories()
                .success(function(pipelineCategories){
                    $scope.pipelineCategories = pipelineCategories;       
                })
                .error(function(msg){
                    console.log(msg);
                });

        };
        $scope.getPipelineCategories();

        $scope.isTextIconShown = function(element){
            return element.iconUrl == null || element.iconUrl == 'http://localhost:8080/img' || typeof element.iconUrl === 'undefined';

        };

        $scope.activeClass = function(pipeline){
            return 'active-pipeline';
        }
        
        $scope.showPipelineCategoriesDialog = function(){
       	 $mdDialog.show({
       	      controller: PipelineCategoriesDialogController,
       	      templateUrl: 'modules/pipelines/templates/managePipelineCategoriesDialog.tmpl.html',
       	      parent: angular.element(document.body),
       	      scope:$scope,
       	      preserveScope: true,
       	      clickOutsideToClose:true       	      
       	    })
       };

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
        	restApi.startPipeline(pipelineId)
                .success(function(data) {
                    $scope.showDialog(data);
                    $scope.getPipelines();

                    $scope.starting = false;
                    
        	    })
                .error(function(data){
                    console.log(data);

                    $scope.starting = false;

                    $scope.showDialog({notifications : [{title : "Network Error", description : "Please check your Network."}]});

                });
        };
        
        $scope.stopPipeline = function(pipelineId) {
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
        
        $scope.getPipelineStatus = function(pipeline) {
        	restApi.getPipelineStatusById(pipeline._id)
            .success(function(data) {

               $scope.pipelineStatus[pipeline._id] = data;
               pipeline.displayStatus = !pipeline.displayStatus;
    	    })
        }

        
        if ($scope.startPipelineDirectly != ""){
           $scope.startPipeline($scope.startPipelineDirectly);
        }

        $scope.showPipeline = function(pipeline){
            pipeline.display = !pipeline.display;

            //clearPipelineDisplay();
            //displayPipeline(pipeline);
        };
        $scope.modifyPipeline = function(pipeline){
            showPipelineInEditor(pipeline);

        };
        $scope.save = function(){
            var options = $('#modalForm').serializeArray();

            if (options.length < $rootScope.state.currentElement.data("JSON").staticProperties.length) {
                toastRightTop("error", "Please enter all parameters");
                return false;
            }
            for (var i = 0; i < options.length; i++) {
                if (options[i].value == "") {
                    toastRightTop("error", "Please enter all parameters");
                    return false;
                }
            }
            saveInStaticProperties(options);
            updatePipeline();

        };
        
        $scope.deletePipeline = function(ev, pipelineId) {
            var confirm = $mdDialog.confirm()
                  .title('Delete pipeline?')
                  .textContent('The pipeline will be removed. ')
                  .targetEvent(ev)
                  .ok('Delete')
                  .cancel('Cancel');
            $mdDialog.show(confirm).then(function() {
            	restApi.deleteOwnPipeline(pipelineId)
                .success(function(data){
                	$scope.getPipelines();
                    console.log(data);
                })
                .error(function(data){
                    console.log(data);
                })
            }, function() {
            	
            });
          };
          
        $scope.addImageOrTextIcon = function($element, json){
            imageChecker.imageExists(json.iconUrl, function(exists){
                if (exists){
                    var $img = $('<img>')
                        .attr("src", json.iconUrl)
                        .addClass('pipeline-display-element-img');
                    $element.append($img);
                }else{
                    var $span = $("<span>")
                        .text(getElementIconText(json.name) || "N/A")
                        .addClass("element-text-icon")
                    $element.append($span);
                }
            });
        }

        function addContextMenu(){

            $('.pipeline-display-element')
                .off('contextmenu')
                .on("contextmenu", function(e){
                    var $invokedOn = $(e.target);
                    while (!$invokedOn.hasClass('pipeline-display-element')) {
                        $invokedOn = $invokedOn.parent();
                    }
                    if($invokedOn.hasClass("sepa") ||  $invokedOn.hasClass("action")){
                        $("#propButton, #divi").show();
                    }else{
                        $("#propButton, #divi").hide();
                    }
                $('#contextMenu')
                    .data("invokedOn", $invokedOn)
                    .show()
                    .css({
                        position: "fixed",
                        left: getLeftLocation(e),
                        top: getTopLocation(e)
                    }).off('click').on('click', function (e) {
                        $(this).hide();
                        var $invokedOn = $(this).data("invokedOn");
                        var $selected = $(e.target);
                        while (!$invokedOn.hasClass('pipeline-display-element')) {
                            $invokedOn = $invokedOn.parent();
                        }
                        if ($selected.get(0) === $('#descrButton').get(0)){ //Description clicked
                            showDescription($invokedOn.data("JSON"));
                        }else if ($selected.get(0) === $('#propButton').get(0)){
                            $rootScope.state.propertyPipeline = $.extend({},$invokedOn.data("pipeline"));
                            $('#customize-content').html(prepareCustomizeModal($invokedOn));
                            $('#customizeModal').modal('show');
                        }

                    });
                    return false;

            });
        }
            $scope.elementTextIcon = function (string){
            var result ="";
            if (string.length <= 4){
                result = string;
            }else {
                var words = string.split(" ");
                words.forEach(function(word, i){
                    result += word.charAt(0);
                });
            }
            return result.toUpperCase();
        };



        function showPipelineInEditor(id){
        	$state.go("streampipes.edit", {pipeline : id});
        }
        
        function PipelineCategoriesDialogController($scope, $mdDialog) {
        	
        	$scope.newCategory = {};
        	$scope.newCategory.categoryName = "";
        	$scope.newCategory.categoryDescription = "";
        	$scope.addSelected = false;
        	$scope.addPipelineToCategorySelected = [];
        	$scope.categoryDetailsVisible = [];
        	$scope.selectedPipelineId = "";
        	
        	$scope.toggleCategoryDetailsVisibility = function(categoryId) {
        		$scope.categoryDetailsVisible[categoryId] = !$scope.categoryDetailsVisible[categoryId];
        	}
        	
        	
            
            $scope.addPipelineToCategory = function(pipelineCategory) {
            	
            	var pipeline = findPipeline(pipelineCategory.selectedPipelineId);
            	if (pipeline.pipelineCategories == undefined) pipeline.pipelineCategories = [];
            	pipeline.pipelineCategories.push(pipelineCategory._id);
            	$scope.storeUpdatedPipeline(pipeline);
            }
            
            $scope.removePipelineFromCategory = function(pipeline, categoryId) {
            	var index = pipeline.pipelineCategories.indexOf(categoryId);
            	pipeline.pipelineCategories.splice(index, 1);
            	$scope.storeUpdatedPipeline(pipeline);
            }
            
            $scope.storeUpdatedPipeline = function(pipeline) {
            	 restApi.updatePipeline(pipeline)
                 .success(function(msg){
                     console.log(msg); 
                     $scope.getPipelines();
                 })
                 .error(function(msg){
                     console.log(msg);
                 });
            }
            
            var findPipeline = function(pipelineId) {
            	var matchedPipeline = {};
            	angular.forEach($scope.pipelines, function(pipeline) {
            		console.log(pipeline._id);
            		if (pipeline._id == pipelineId) matchedPipeline = pipeline;
            	});
            	return matchedPipeline;
            }
           
            $scope.addPipelineCategory = function() {
            	restApi.storePipelineCategory($scope.newCategory)
            	 .success(function(data){
                     console.log(data);
                     $scope.getPipelineCategories();
                     $scope.addSelected = false;
                 })
                 .error(function(msg){
                     console.log(msg);
                 });
            }
            
            $scope.showAddToCategoryInput = function(categoryId, show) {
            	$scope.addPipelineToCategorySelected[categoryId] = show;
            	$scope.categoryDetailsVisible[categoryId] = true;
            }
            
            $scope.deletePipelineCategory = function(pipelineId) {
            	restApi.deletePipelineCategory(pipelineId)
            	 .success(function(data){
                     console.log(data);
                     $scope.getPipelineCategories();
                 })
                 .error(function(msg){
                     console.log(msg);
                 });
            }         
        	
        	$scope.showAddInput = function() {
        		$scope.addSelected = true;
        		$scope.newCategory.categoryName="";
            	$scope.newCategory.categoryDescription="";
        	}
        	
	      	$scope.hide = function() {
	      	  $mdDialog.hide();
	      	};
	      	
	      	$scope.cancel = function() {
	      	    $mdDialog.cancel();
	      	};
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

        function getLeftLocation(e) {

            var menuWidth = $('#contextMenu').width();
            var mouseWidth = e.pageX;
            var pageWidth = $(window).width();

            // opening menu would pass the side of the page
            if (mouseWidth + menuWidth > pageWidth && menuWidth < mouseWidth) {
                return mouseWidth - menuWidth;
            }
            return mouseWidth;
        }

        function getTopLocation(e) {

            var menuHeight = $('#contextMenu').height();

            var mouseHeight = e.pageY - $(window).scrollTop();
            var pageHeight = $(window).height();

            if (mouseHeight + menuHeight > pageHeight && menuHeight < mouseHeight) {
                return mouseHeight - menuHeight ;
            }
            return mouseHeight ;

        }

        function prepareCustomizeModal(element) {
            $rootScope.state.currentElement = element;
            var string = "";
            textInputFields.length = 0;
            if (element.data("JSON").staticProperties != null && element.data("JSON").staticProperties != []) {
                var staticPropertiesArray = element.data("JSON").staticProperties;

                var textInputCount = 0;
                var radioInputCount = 0;
                var selectInputCount = 0;
                var checkboxInputCount = 0;

                for (var i = 0; i < staticPropertiesArray.length; i++) {
                    switch (staticPropertiesArray[i].input.properties.elementType) {
                        case "TEXT_INPUT":
                            var textInput = {};
                            if (staticPropertiesArray[i].input.properties.datatype != undefined)
                            {
                                textInput.fieldName = "textinput" +i;
                                textInput.propertyName = staticPropertiesArray[i].input.properties.datatype;
                                textInputFields.push(textInput);
                            }
                            string += getTextInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].name, textInputCount, staticPropertiesArray[i].input.properties.value);
                            textInputCount++;
                            continue;
                        case "RADIO_INPUT":
                            string += getRadioInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].input.properties.options, radioInputCount);
                            radioInputCount++;
                            continue;
                        case "CHECKBOX":
                            string += getCheckboxInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].input.properties.options, i);
                            checkboxInputCount++;
                            continue;
                        case "SELECT_INPUT":
                            string += getSelectInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].input.properties.options, selectInputCount);
                            selectInputCount++;

                    }
                }
            }

            return string;
        }
        function saveInStaticProperties(options) {
            for (var i = 0; i < options.length; i++) {
                switch ($rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.elementType) {

                    case "RADIO_INPUT" :
                    case "SELECT_INPUT" :
                        for (var j = 0; j < $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options.length; j++) {
                            if ($rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].humanDescription == options[i].value) {
                                $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = true;
                            } else {
                                $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = false;
                            }
                        }
                        continue;
                    case "CHECKBOX" :
                        for (var j = 0; j < $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options.length; j++) {
                            if ($("#" + options[i].value + " #checkboxes-" + i + "-" + j).is(':checked')) {
                                $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = true;
                            } else {
                                $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = false;
                            }
                        }
                        continue;
                    case "TEXT_INPUT":
                        $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.value = options[i].value;

                }
            }
            var changed = false;
            $rootScope.state.propertyPipeline.sepas.forEach(function(sepa, i, sepas){
               if (sepa.DOM == $rootScope.state.currentElement.data("JSON").DOM){
                   sepa.staticProperties = $rootScope.state.currentElement.data("JSON").staticProperties;
                   changed = true;
               }
            });
            if (!changed){
                if ($rootScope.state.propertyPipeline.action.DOM == $rootScope.state.currentElement.data("JSON").DOM){
                    $rootScope.state.propertyPipeline.action.staticProperties = $rootScope.state.currentElement.data("JSON").staticProperties;
                }else{
                    alert("Something went wrong.");
                }
            }
            //toastRightTop("success", "Parameters saved");
        }

            function updatePipeline(){
                restApi.updatePartialPipeline($rootScope.state.propertyPipeline)
                    .then(function(data){
                        $('#customizeModal').modal('hide');
                        $scope.showDialog(data);
                    }, function(data){
                        console.log(data);
                    })
            }

    }])
    .directive('myStreamDataAndImageBind', function(){
        return {
            restrict: 'A',
            link: function(scope, elem, attrs){
                scope.addImageOrTextIcon(elem, scope.stream);
                elem.data("JSON", scope.stream);
                elem.data("pipeline", scope.pipeline);
                elem.attr({'data-toggle' : "tooltip", 'data-placement': "top", 'title' : scope.stream.name});
                elem.tooltip();


            }
        }
    })
    .directive('mySepaDataAndImageBind', function(){
        return {
            restrict: 'A',
            link: function(scope, elem, attrs){
                scope.addImageOrTextIcon(elem, scope.sepa);
                elem.data("JSON", scope.sepa);
                elem.data("pipeline", scope.pipeline);
                elem.attr({'data-toggle' : "tooltip", 'data-placement': "top", 'title' : scope.sepa.name});
                elem.tooltip();


            }
        }
    })
    .directive('myActionDataAndImageBind', function(){
        return {
            restrict: 'A',
            link: function(scope, elem, attrs){
                scope.addImageOrTextIcon(elem, scope.pipeline.action);
                elem.data("JSON", scope.pipeline.action);
                elem.data("pipeline", scope.pipeline);
                elem.attr({'data-toggle' : "tooltip", 'data-placement': "top", 'title' : scope.pipeline.action.name});
                elem.tooltip();
            }
        }
    })
    .filter('pipelineCategoryFilter', function() { 
    	 return function(pipelines, categoryId) {
    		 console.log(categoryId);
    		 var result = [];
    		 var showAll = false;
    		 if (categoryId == "") showAll = true;
    		 angular.forEach(pipelines, function(pipeline) {
    			 if (showAll) result.push(pipeline);
    			 else {
	    			 angular.forEach(pipeline.pipelineCategories, function(category) {
	    				 if (category == categoryId) result.push(pipeline);
	    			 })
    			 }
    		 })
    		 return result;
    	 };
    })
    .filter('categoryAlreadyInPipelineFilter', function() { 
    	 return function(pipelines, categoryId) {
    		 var result = [];
    		 angular.forEach(pipelines, function(pipeline) {
    			 var inPipeline = false;
    			 angular.forEach(pipeline.pipelineCategories, function(category) {
    				 if (category == categoryId) inPipeline = true;
    			 })
    			 if (!inPipeline) result.push(pipeline);
    		 })
    		 return result;
    	 };
    })

