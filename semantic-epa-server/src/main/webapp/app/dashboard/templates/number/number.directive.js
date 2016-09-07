import Widgets from '../../widgets.service.js'
'use strict';
numberWidget.$inject = ['Widgets'];

export default function numberWidget(Widgets) {
	return {
		restrict: 'A',
		replace: true,
		templateUrl: 'app/dashboard/templates/number/number.html',
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
};
