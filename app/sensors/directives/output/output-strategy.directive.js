outputStrategy.$inject = [];

export default function outputStrategy() {

	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/output/output-strategy.tmpl.html',
		scope : {
			strategies : "=strategies",
			disabled : "=disabled"
		},
		link: function($scope, element, attrs) {

			$scope.outputStrategyTypes = [{label : "Append", "type" : "org.streampipes.model.impl.output.AppendOutputStrategy"},
				{label : "Custom", "type" : "org.streampipes.model.impl.output.CustomOutputStrategy"},
				{label : "Fixed", "type" : "org.streampipes.model.impl.output.FixedOutputStrategy"},
				{label : "List", "type" : "org.streampipes.model.impl.output.ListOutputStrategy"},
				{label : "Keep", "type" : "org.streampipes.model.impl.output.RenameOutputStrategy"}];

			$scope.selectedOutputStrategy = $scope.outputStrategyTypes[0].type;

			$scope.addOutputStrategy = function(strategies) {   
				if (strategies == undefined) $scope.strategies = [];
				$scope.strategies.push(getNewOutputStrategy());
			}

			$scope.removeOutputStrategy = function(strategies, index) {
				strategies.splice(index, 1);
			};

			var getNewOutputStrategy = function() {
				if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[0].type)
					return {"type" : $scope.outputStrategyTypes[0].type, "properties" : {"eventProperties" : []}};
				else if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[1].type)
					return {"type" : $scope.outputStrategyTypes[1].type, "properties" : {"eventProperties" : []}};
				else if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[2].type)
					return {"type" : $scope.outputStrategyTypes[2].type, "properties" : {"eventProperties" : []}};
				else if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[3].type)
					return {"type" : $scope.outputStrategyTypes[3].type, "properties" : {}};
				else if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[4].type)
					return {"type" : $scope.outputStrategyTypes[4].type, "properties" : {}};

			}

			$scope.getType = function(strategy) {
				var label;
				angular.forEach($scope.outputStrategyTypes, function(value) {
					if (value.type == strategy.type) label = value.label;
				});
				return label;
			};
		}		
	}
};
