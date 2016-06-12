angular
    .module('streamPipesApp')
    .directive('deploymentType', function(restApi, measurementUnitsService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/deployment-type.tmpl.html',
    		scope : {
    			disabled : "=",
    			deployment : "="
    		},
    		controller: function($scope, $element) {
    					
    			
    		}
    	}
    }).directive('sinkDeployment', function(restApi, deploymentService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/sink-deployment.tmpl.html',
    		scope : {
    			disabled : "=",
    			element : "="
    		},
    		controller: function($scope, $element) {
    			$scope.deployment = {};
    			$scope.deployment.elementType = "Sec";
    			
    			$scope.resultReturned = false;
    			$scope.loading = false;
    			$scope.jsonld = "";
    			
    			$scope.generateImplementation = function() {			
    				deploymentService.generateImplementation($scope.deployment, $scope.element)
    					.success(function(data, status, headers, config) {
    					    $scope.openSaveAsDialog($scope.deployment.artifactId +".zip", data, "application/zip");
    					    $scope.loading = false;
    				  }).error(function(data, status, headers, config) {
    				    console.log(data);
    				    $scope.loading = false;
    				  });
    			};
    			
    			$scope.generateDescription = function() {
    				$scope.loading = true;
    				deploymentService.generateDescription($scope.deployment, $scope.element)
    					.success(function(data, status, headers, config) {
    					   // $scope.openSaveAsDialog($scope.element.name +".jsonld", data, "application/json");
    					    $scope.loading = false;
    					    $scope.resultReturned = true;
    					    $scope.jsonld = String.fromCharCode.apply(null, new Uint8Array(data));
    				  }).
    				  error(function(data, status, headers, config) {
    				    console.log(data);
    				    $scope.loading = false;
    				  });
    			}
    			  	
    		  	$scope.openSaveAsDialog = function(filename, content, mediaType) {
    			    var blob = new Blob([content], {type: mediaType});
    			    saveAs(blob, filename);
    			}  	    			
    		}
    	}
    }).directive('streamDeployment', function(restApi, deploymentService) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/stream-deployment.tmpl.html',
    		scope : {
    			element : "=",
    			disabled : "=disabled"
    		},
    		
    		controller: function($scope, $element) {
    			
    			$scope.deployment = {};
    			$scope.deployment.elementType = "Sep";
    			
    			$scope.resultReturned = false;
    			$scope.loading = false;
    			$scope.jsonld = "";
    			
    			$scope.generateImplementation = function() {			
    				deploymentService.generateImplementation($scope.deployment, $scope.element)
    					.success(function(data, status, headers, config) {
    					    $scope.openSaveAsDialog($scope.deployment.artifactId +".zip", data, "application/zip");
    					    $scope.loading = false;
    				  }).error(function(data, status, headers, config) {
    				    console.log(data);
    				    $scope.loading = false;
    				  });
    			};
    			
    			$scope.generateDescription = function() {
    				$scope.loading = true;
    				deploymentService.generateDescription($scope.deployment, $scope.element)
    					.success(function(data, status, headers, config) {
    					    $scope.loading = false;
    					    $scope.resultReturned = true;
    					    $scope.jsonld = String.fromCharCode.apply(null, new Uint8Array(data));
    				  }).
    				  error(function(data, status, headers, config) {
    				    console.log(data);
    				    $scope.loading = false;
    				  });
    			}
    			  	
    		  	$scope.openSaveAsDialog = function(filename, content, mediaType) {
    			    var blob = new Blob([content], {type: mediaType});
    			    saveAs(blob, filename);
    			}  	    		
    			
    		}
    	}
    }).directive('sepaDeployment', function(restApi, deploymentService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/sepa-deployment.tmpl.html',
    		scope : {
    			disabled : "=",
    			element : "="
    		},
    		controller: function($scope, $element) {
    			
    			$scope.deployment = {};
    			$scope.deployment.elementType = "Sepa";
    			
    			$scope.resultReturned = false;
    			$scope.loading = false;
    			$scope.jsonld = "";
    			
    			$scope.generateImplementation = function() {			
    				deploymentService.generateImplementation($scope.deployment, $scope.element)
    					.success(function(data, status, headers, config) {
    					    $scope.openSaveAsDialog($scope.deployment.artifactId +".zip", data, "application/zip");
    					    $scope.loading = false;
    				  }).error(function(data, status, headers, config) {
    				    console.log(data);
    				    $scope.loading = false;
    				  });
    			};
    			
    			$scope.generateDescription = function() {
    				$scope.loading = true;
    				deploymentService.generateDescription($scope.deployment, $scope.element)
    					.success(function(data, status, headers, config) {
    					   // $scope.openSaveAsDialog($scope.element.name +".jsonld", data, "application/json");
    					    $scope.loading = false;
    					    $scope.resultReturned = true;
    					    $scope.jsonld = String.fromCharCode.apply(null, new Uint8Array(data));
    				  }).
    				  error(function(data, status, headers, config) {
    				    console.log(data);
    				    $scope.loading = false;
    				  });
    			}
    			  	
    		  	$scope.openSaveAsDialog = function(filename, content, mediaType) {
    			    var blob = new Blob([content], {type: mediaType});
    			    saveAs(blob, filename);
    			}  	
    		}
    	}
    }).directive('generatedElementDescription', function(restApi, deploymentService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/generated-element-description.tmpl.html',
    		scope : {
    			jsonld : "=",
    			java : "=",
    			element : "=",
    		},
    		controller: function($scope, $element) {
    			
    			$scope.downloadJsonLd = function() {
    				$scope.openSaveAsDialog($scope.element.name +".jsonld", $scope.jsonld, "application/json");
    			}
    			
    			$scope.openSaveAsDialog = function(filename, content, mediaType) {
    			    var blob = new Blob([content], {type: mediaType});
    			    saveAs(blob, filename);
    			}  	
    			
    		}
    	}
    });
        		