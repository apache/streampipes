import { WidgetInstances } from '../../widget-instances.service'
'use strict';
declare const require: any;
verticalbarWidget.$inject = ['WidgetInstances'];

export default function verticalbarWidget(WidgetInstances) {
	return {
		restrict: 'A',
		replace: true,
		template: require('./verticalbar.html'),
		scope: {
			data: '=',
			widgetId: '@'
		},
		controller: function ($scope) {
			WidgetInstances.get($scope.widgetId).then(function(widgetConfig) {
				$scope.selectedNumberProperty = widgetConfig.visualisation.schema.selectedNumberProperty.properties.runtimeName;
				$scope.min = widgetConfig.visualisation.config.min;
				$scope.max = widgetConfig.visualisation.config.max;
			});

			// TODO replace with min/max values
			$scope.printValue = function(value) {
				return 100 - percent(0, 100, value);
			}

			var percent = function(min, max, current) {
				return 100 * (current - $scope.min) / ($scope.max - $scope.min);
			}


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
