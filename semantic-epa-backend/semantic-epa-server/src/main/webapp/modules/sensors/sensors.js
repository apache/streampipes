angular.module('streamPipesApp')
.controller('SensorCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast, $animate, $mdDialog) {

	$scope.editingDisabled = true;
	
	$scope.sepas = [];
	$scope.sources = [];
	$scope.actions = [];
		
	$scope.selectedSepa;
	$scope.selectedStream;
	$scope.selectedSource;
	$scope.selectedAction;
	
	$scope.sepaSelected = false;
	$scope.sourceSelected = false;
	$scope.streamSelected = false;
	$scope.actionSelected = false;
	
	$scope.toggleEditMode = function() {
		$scope.editingDisabled = !$scope.editingDisabled;
	}
	
	$scope.addNewSepa = function() {
		$scope.selectedSepa = {"eventStreams" : [], "name" : "", "staticProperties" : []};
		$scope.sepaSelected = true;
		$scope.editingDisabled = false;
	}
	
	$scope.addNewAction = function() {
		$scope.selectedAction = {"eventStreams" : [], "name" : "", "staticProperties" : []};
		$scope.actionSelected = true;
		$scope.editingDisabled = false;
	}
	
	$scope.loadSepaDetails = function(uri, keepIds, editingDisabled) {
		restApi.getSepaDetailsFromOntology(uri, keepIds)
			.success(function(sepaData){
				$scope.selectedSepa = sepaData;
                $scope.sepaSelected = true;
                $scope.editingDisabled = editingDisabled;
            })
            .error(function(msg){
                console.log(msg);
            });
	}
	
	$scope.loadActionDetails = function(uri, keepIds, editingDisabled) {
		restApi.getActionDetailsFromOntology(uri, keepIds)
			.success(function(actionData){
				$scope.selectedAction = actionData;
                $scope.actionSelected = true;
                $scope.editingDisabled = editingDisabled;
            })
            .error(function(msg){
                console.log(msg);
            });
	}
	
	$scope.loadSourceDetails = function(index) {
		$scope.sourceSelected = true;
		$scope.streamSelected = false;
		$scope.selectedSource = $scope.sources[index];
	}
	
	$scope.loadStreamDetails = function(index, streamId) {
		$scope.sourceSelected = false;
		$scope.streamSelected = true;
		$scope.selectedStream = $scope.sources[index].eventStreams[streamId];
	}
	
	$scope.loadSepas = function(){
        restApi.getSepasFromOntology()
            .success(function(sepaData){
                $scope.sepas = sepaData;
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.loadSources = function(){
        restApi.getSourcesFromOntology()
            .success(function(sources){
                $scope.sources = sources;
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.loadActions = function(){
        restApi.getActionsFromOntology()
            .success(function(actions){
                $scope.actions = actions;
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    
    $scope.openDownloadDialog = function(elementId, elementData){
		 $mdDialog.show({
	   	      controller: DownloadDialogController,
	   	      templateUrl: 'modules/sensors/templates/downloadDialog.tmpl.html',
	   	      parent: angular.element(document.body),
	   	      clickOutsideToClose:true,
	   	      scope:$scope,
	   	      preserveScope:true,
		   	  locals : {
		   		  elementId : elementId,
		   		  elementData : elementData
		      }
	   	    })
	 }
    
    $scope.loadSepas();
    $scope.loadActions();
    $scope.loadSources();
    
});

function DownloadDialogController($scope, $mdDialog, restApi, elementId, elementData, $http) {

	$scope.elementId = elementId;
	$scope.deployment = {};
	$scope.deployment.elemendId = elementId;
	
	$scope.loading = false;
	
	console.log("ELEMENTDATA");
	console.log(elementData);
	
	$scope.generateImplementation = function() {	
		$scope.loading = true;
		$http({method: 'POST', responseType : 'arraybuffer', headers: {'Accept' : 'application/zip', 'Content-Type': undefined}, url: '/semantic-epa-backend/api/v2/users/riemer@fzi.de/deploy/implementation', data : getFormData()}).
		  success(function(data, status, headers, config) {
			    $scope.openSaveAsDialog($scope.deployment.artifactId +".zip", data, "application/zip");
			    $scope.loading = false;
		  }).
		  error(function(data, status, headers, config) {
		    console.log(data);
		    $scope.loading = false;
		  });
	};
	
	$scope.generateDescription = function() {
		$scope.loading = true;
		$http({method: 'POST', responseType : 'arraybuffer', headers: {'Accept' : 'application/json', 'Content-Type': undefined}, url: '/semantic-epa-backend/api/v2/users/riemer@fzi.de/deploy/description', data : getFormData()}).
		  success(function(data, status, headers, config) {
			    $scope.openSaveAsDialog(elementData.name +".jsonld", data, "application/json");
			    $scope.loading = false;
		  }).
		  error(function(data, status, headers, config) {
		    console.log(data);
		    $scope.loading = false;
		  });
	}
	
	var getFormData = function() {
		var formData = new FormData();
		formData.append("config", angular.toJson($scope.deployment));
		formData.append("model", angular.toJson(elementData));
		return formData;
	}
	
	$scope.hide = function() {
  		$mdDialog.hide();
  	};
  	
  	$scope.cancel = function() {
  	    $mdDialog.cancel();
  	};
  	
  	$scope.openSaveAsDialog = function(filename, content, mediaType) {
	    var blob = new Blob([content], {type: mediaType});
	    saveAs(blob, filename);
	}
}