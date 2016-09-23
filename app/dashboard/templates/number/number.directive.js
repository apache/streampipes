import WidgetInstances from '../../widget-instances.service.js'
'use strict';
numberWidget.$inject = ['WidgetInstances'];

export default function numberWidget(WidgetInstances) {
	return {
		restrict: 'A',
		replace: true,
		templateUrl: 'app/dashboard/templates/number/number.html',
		scope: {
			data: '=',
			widgetId: '@'
		},
		controller: function ($scope) {
			WidgetInstances.get($scope.widgetId).then(function(data) {
				$scope.selectedNumberProperty = data.visualisation.schema.selectedNumberProperty.properties.runtimeName;
			});
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
