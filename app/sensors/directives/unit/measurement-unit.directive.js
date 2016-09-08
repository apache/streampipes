measurementUnit.$inject = ['measurementUnitsService'];

export default function measurementUnit(measurementUnitsService) {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/unit/measurement-unit.tmpl.html',
		scope : {
			disabled : "=",
			property : "="
		},
		controller: function($scope, $element)  {

			var query = {};
			$scope.selectedItem = "";
			$scope.items = measurementUnitsService.getUnits();

			$scope.querySearch = querySearch;
			$scope.selectedItemChange = selectedItemChange;
			$scope.searchTextChange   = searchTextChange;

			if ($scope.property != undefined && $scope.property != "") {
				angular.forEach($scope.items, function(item) {
					if (item.resource == $scope.property) $scope.selectedItem = item;
				});
			}

			function querySearch (query) {
				var results = [];

				angular.forEach($scope.items, function(item) {
					if (query == undefined || item.label.substring(0, query.length) === query) results.push(item);
				})

				return results;	     
			}

			function searchTextChange(text) {

			}
			function selectedItemChange(item) {
				if (item != undefined) $scope.property = item.resource;
			}	
		}
	}
};
