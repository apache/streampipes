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
    			
    			$scope.downloadJava = function() {
    				$scope.openSaveAsDialog($scope.element.name +".java", $scope.jsonld, "application/java");
    			}
    			
    			$scope.openSaveAsDialog = function(filename, content, mediaType) {
    			    var blob = new Blob([content], {type: mediaType});
    			    saveAs(blob, filename);
    			}  	
    			
    		}
    	}
    }).directive('generatedElementImplementation', function(restApi, deploymentService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/generated-element-implementation.tmpl.html',
    		scope : {
    			zipFile : "=",
    			element : "=",
    		},
    		controller: function($scope, $element) {
    			    		
    			$scope.extractedFiles = [];
    			$scope.currentFileName = "";
    			$scope.currentFileContents = "";
    			$scope.loadingCompleted = false;
    			
    			$scope.new_zip = new JSZip();
    	
    			$scope.new_zip.loadAsync($scope.zipFile)
    			.then(function(zip) {

    			    angular.forEach(zip.files, function(file) {
    			    	var filename = file.name;
    			    	$scope.extractedFiles.push({"fileNameLabel" : $scope.getFileName(filename), 
    			    		"fileNameDescription" : $scope.getDirectory(filename), 
    			    		"fileName" : filename, 
    			    		"fileContents" : file});
    			    })    
    			    console.log($scope.extractedFiles);
    			});
    			
    			$scope.openFile = function(file) {
    				$scope.loadingCompleted = false;
    				$scope.currentFileName = file.fileName;
    				file.fileContents.async("string")
    				.then(function (content) {
    					$scope.currentFileContents = content;
    					$scope.loadingCompleted = true;
    				});;
    			}
    			
    			$scope.getLanguage = function(filename) {
    				if (filename.endsWith("java")) return "java";
    				else if (filename.endsWith("xml")) return "xml";
    				else return "";
    			}
    			
    			$scope.getFileName = function(filename) {
    				if (/.+\\/gi.test(filename))
    					return filename.replace(/.+\\/g, "");
    				else
    					return filename;
    			}
    			
    			$scope.getDirectory = function(filename) {
    				if (/.+\\/gi.test(filename)) {
    					var directory = /.+\\/gi.exec(filename)[0];
    					return directory.replace(/\\/g, "/");
    				}
    				else return "/";
    			}
    			
    			$scope.downloadZip = function() {
    				$scope.openSaveAsDialog($scope.element.name +".zip", $scope.zipFile, "application/zip");
    			}
    			
    			$scope.openSaveAsDialog = function(filename, content, mediaType) {
    			    var blob = new Blob([content], {type: mediaType});
    			    saveAs(blob, filename);
    			}  	
    			
    		}
    	}
    }).directive('deployment', function(restApi, deploymentService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/deployment.tmpl.html',
    		scope : {
    			disabled : "=",
    			element : "=",
    			deploymentSettings :"=",
    		},
    		controller: function($scope, $element) {
    			
    			$scope.deployment = {};
    			$scope.deployment.elementType = $scope.deploymentSettings.elementType;
    			
    			$scope.resultReturned = false;
    			$scope.loading = false;
    			$scope.jsonld = "";
    			$scope.zipFile = "";
    			
    			$scope.generateImplementation = function() {			
    				deploymentService.generateImplementation($scope.deployment, $scope.element)
    					.success(function(data, status, headers, config) {
    					    //$scope.openSaveAsDialog($scope.deployment.artifactId +".zip", data, "application/zip");
    						$scope.resultReturned = true;
    					    $scope.loading = false;
    					    $scope.zipFile = data;
    				  }).error(function(data, status, headers, config) {
    				    console.log(data);
    				    $scope.loading = false;
    				  });
    			};
    			
    			$scope.generateDescription = function() {
    				$scope.loading = true;
    				deploymentService.generateDescriptionJava($scope.deployment, $scope.element)
    					.success(function(data, status, headers, config) {
    					   // $scope.openSaveAsDialog($scope.element.name +".jsonld", data, "application/json");
    					    $scope.loading = false;
    					    $scope.resultReturned = true;
    					    $scope.java = data;
    				}).
    				  error(function(data, status, headers, config) {
    				    console.log(data);
    				    $scope.loading = false;
    				  });
    				deploymentService.generateDescriptionJsonld($scope.deployment, $scope.element)
					.success(function(data, status, headers, config) {
					   // $scope.openSaveAsDialog($scope.element.name +".jsonld", data, "application/json");
					    $scope.loading = false;
					    $scope.resultReturned = true;
					    $scope.jsonld = JSON.stringify(data, null, 2);
					    console.log($scope.jsonld);
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
    });
        		