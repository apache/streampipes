export default function streamQualityDefinitions() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/quality/stream-quality-definitions.tmpl.html',
		scope : {
			disabled : "=",
			property : "=",
			runtimeType :"="
		},
		controller: function($scope, $element)  {

			$scope.qualities = [{label : "Frequency", "description" : "", "type" : "de.fzi.cep.sepa.model.impl.quality.Frequency"},
				{label : "Latency", "description" : "", "type" : "de.fzi.cep.sepa.model.impl.quality.Latency"}];



			$scope.add = function() {
				if ($scope.property.properties == undefined) {
					$scope.property.properties = {};
					$scope.property.properties.runtimeValues = [];
				}
				$scope.property.properties.runtimeValues.push("");
			}

			$scope.remove = function(runtimeValues, propertyIndex) {
				runtimeValues.splice(propertyIndex, 1);
			};
		}
	}
};
