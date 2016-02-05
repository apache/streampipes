angular.module('streamPipesApp')
.controller('SensorCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast, $animate, $mdDialog, $filter) {

	$scope.editingDisabled = true;
	
	$scope.sepas = [];
	$scope.sources = [];
	$scope.actions = [];
		
	$scope.selectedSepa;
	$scope.selectedSource;
	$scope.selectedAction;
	$scope.selectedStream;
	
	$scope.sepaSelected = false;
	$scope.sourceSelected = false;
	$scope.actionSelected = false;
	$scope.streamSelected = false;
	
	$scope.selectedTab = "SOURCES";
	
	$scope.setSelectedTab = function(type) {
		$scope.selectedTab = type;
	}
	
	$scope.toggleEditMode = function() {
		$scope.editingDisabled = !$scope.editingDisabled;
	
	}
	
	$scope.removeStream = function(eventStreams, stream) {
		eventStreams.splice(stream, 1);
	}
	
	$scope.loadStreamDetails = function(stream, editingDisabled) {
		$scope.editingDisabled = editingDisabled;
		$scope.streamSelected = true;
		$scope.selectedStream = stream;
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
	
	$scope.addNewSource = function() {
		$scope.selectedSource = undefined;
		$scope.selectedSource = {"eventStreams" : [], "name" : ""};
		$scope.sourceSelected = true;
		$scope.streamSelected = false;
		$scope.selectedStream = "";
		$scope.editingDisabled = false;
	}
	
	$scope.addStream = function(element) {
		element.push({"name" : "", "eventSchema" : {"eventProperties" : []}, "eventGrounding" : {"transportFormats" : [], "transportProtocols" : []}});
		$scope.loadStreamDetails(element[element.length-1]);
	}
	
	$scope.cloneStream = function(eventStreams, stream) {
		var clonedStream = angular.copy(stream);
		clonedStream.uri = "";
		eventStreams.push(clonedStream);
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
		$scope.editingDisabled = true;
		$scope.sourceSelected = true;
		$scope.selectedSource = $scope.sources[index];
	}
	
	$scope.loadSepas = function(){
        restApi.getSepasFromOntology()
            .success(function(sepaData){
                $scope.sepas = $filter('orderBy')(sepaData, "name", false);;
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.getSourceDetailsFromOntology = function(sourceId) {
    	restApi.getSourceDetailsFromOntology(sourceId, false) 
    		.success(function(source){
    			$scope.editingDisabled = false;
    			$scope.sourceSelected = true;
    			$scope.selectedSource = source;
    			$scope.selectedSource.uri = "";		
    			angular.forEach($scope.selectedSource.eventStreams, function(stream, key) {
    				stream.uri = "";
    			});
    		})
	        .error(function(msg){
	            console.log(msg);
	        });
    }
    
    $scope.loadSources = function(){
        restApi.getSourcesFromOntology()
            .success(function(sources){
            	
                $scope.sources = $filter('orderBy')(sources, "name", false);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.loadActions = function(){
        restApi.getActionsFromOntology()
            .success(function(actions){
                $scope.actions = $filter('orderBy')(actions, "name", false);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.openSourceOptionsDialog = function(elementId, elementData, elementType){
		 $mdDialog.show({
	   	      controller: SourceOptionsDialogController,
	   	      templateUrl: 'modules/sensors/templates/sourceOptionsDialog.tmpl.html',
	   	      parent: angular.element(document.body),
	   	      clickOutsideToClose:true,
	   	      scope:$scope,
	   	      preserveScope:true,
		   	  locals : {
		   		  elementId : elementId,
		   		  elementData : elementData,
		   		  elementType : elementType
		      }
	   	    })
	 }
    
    
    $scope.openDownloadDialog = function(elementId, elementData, elementType){
		 $mdDialog.show({
	   	      controller: DownloadDialogController,
	   	      templateUrl: 'modules/sensors/templates/downloadDialog.tmpl.html',
	   	      parent: angular.element(document.body),
	   	      clickOutsideToClose:true,
	   	      scope:$scope,
	   	      preserveScope:true,
		   	  locals : {
		   		  elementId : elementId,
		   		  elementData : elementData,
		   		  elementType : elementType
		      }
	   	    })
	 }
    
    $scope.loadSepas();
    $scope.loadActions();
    $scope.loadSources();
    
});

function DownloadDialogController($scope, $mdDialog, restApi, elementId, elementData, elementType, $http, $rootScope) {

	$scope.elementId = elementId;
	$scope.deployment = {};
	$scope.deployment.elemendId = elementId;
	$scope.deployment.elementType = elementType;
	
	$scope.loading = false;
		
	$scope.generateImplementation = function() {	
		$scope.loading = true;
		$http({method: 'POST', responseType : 'arraybuffer', headers: {'Accept' : 'application/zip', 'Content-Type': undefined}, url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/implementation', data : getFormData()}).
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
		$http({method: 'POST', responseType : 'arraybuffer', headers: {'Accept' : 'application/json', 'Content-Type': undefined}, url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/description', data : getFormData()}).
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

function SourceOptionsDialogController($scope, $mdDialog, restApi, elementId, elementData, elementType, $http, $rootScope) {

	$scope.elementId = elementId;
	$scope.deployment = {};
	$scope.deployment.elemendId = elementId;
	$scope.deployment.elementType = elementType;
	
	$scope.loading = false;
	$scope.directImportResult = false;
	$scope.result = "";
	
	$scope.directImport = function() {
		console.log(elementData);
		$scope.loading = true;
		$http({method: 'POST', headers: {'Accept' : 'application/json', 'Content-Type': undefined}, url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/import', data : getFormData()}).
		  success(function(data, status, headers, config) {
			  	$scope.loading = false;
			  	$scope.result = data;
			  	$scope.directImportResult = true;
		  }).
		  error(function(data, status, headers, config) {
		    console.log(data);
		    $scope.loading = false;
		  });
	}
	
	
	$scope.generateDescription = function() {
		console.log(elementData);
		$scope.loading = true;
		$http({method: 'POST', responseType : 'arraybuffer', headers: {'Accept' : 'application/json', 'Content-Type': undefined}, url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/description', data : getFormData()}).
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