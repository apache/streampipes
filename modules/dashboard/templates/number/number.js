'use strict';

angular.module('streamPipesApp')
	.directive('numberWidget',['Widgets', function (Widgets) {
		return {
			restrict: 'A',
			replace: true,
			templateUrl: 'modules/dashboard/templates/number/number.html',
			scope: {
				data: '=',
				widgetId: '@'
			},
			controller: function ($scope) {
				var widgetConfig = Widgets.get($scope.widgetId);
				$scope.selectedNumberProperty = widgetConfig.vis.schema.selectedNumberProperty.properties.runtimeName;
			},
			link: function postLink(scope) {
				scope.$watch('data', function (data) {
					if (data) {
						scope.item = data;
					}
				});
			}
		};
	}]);
