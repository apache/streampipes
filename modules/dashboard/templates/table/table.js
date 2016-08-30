'use strict';

angular.module('streamPipesApp')
	.directive('tableWidget',['Widgets', function (Widgets) {
		return {
			restrict: 'A',
			replace: true,
			templateUrl: 'modules/dashboard/templates/table/table.html',
			scope: {
				data: '=',
				widgetId: '@'
			},
			controller: function ($scope) {
				$scope.tableOptions = {
					initialSorts: [
						{ id: 'value', dir: '-' }
					]
				};


				//Add the colums that where selected by the user
				var widgetConfig = Widgets.get($scope.widgetId);
				$scope.columns = [];
				angular.forEach(widgetConfig.vis.schema.eventProperties, function(prop) {

					if (prop.isSelected) {
						var name = prop.properties.runtimeName;
						$scope.columns.push({ id: name, key: name, label: name });
					}
				});


			},
			link: function postLink(scope) {
				scope.$watch('data', function (data) {
					if (data) {
						scope.items = data;
					}
				});
			}
		};
	}]);
