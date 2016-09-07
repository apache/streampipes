deployment.$inject = ['deploymentService'];

export default function deployment(deploymentService) {
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
				$scope.resultReturned = false;
				$scope.loading = true;
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
};
