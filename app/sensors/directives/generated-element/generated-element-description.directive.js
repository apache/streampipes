export default function generatedElementDescription() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/generated-element/generated-element-description.tmpl.html',
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
};
