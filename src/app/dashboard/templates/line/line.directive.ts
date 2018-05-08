import { WidgetInstances } from '../../widget-instances.service'
'use strict';
lineWidget.$inject = ['WidgetInstances'];

export default function lineWidget(WidgetInstances) {
	return {
		restrict: 'A',
		replace: true,
		templateUrl: 'line.html',
		scope: {
			data: '=',
			widgetId: '@'
		},
		controller: function ($scope) {
			WidgetInstances.get($scope.widgetId).then(function(data) {
				$scope.widgetConfig = data.visualisation.schema.config;
			});
			$scope.myChart = null;
		},
		link: function postLink(scope, element) {
				scope.$watch('data', function (data) {
					if (data) {
						if (!scope.myChart) {
							scope.myChart = element.epoch({
								type: 'time.line',
								data: [{ label: "Series 1", values: []}],
								axes: ['bottom', 'left'],
								range: [scope.widgetConfig.range.min, scope.widgetConfig.range.max]
							});
						} else {
							var time_value = data[scope.widgetConfig.selectedTimestampMapping.properties.runtimeName];
							// TODO find a better solution to format the long date for the epoch library
							time_value = parseInt(time_value.toString().slice(0, -3));
							var y_value = data[scope.widgetConfig.selectedNumberMapping.properties.runtimeName];

							scope.myChart.push([{time: time_value, y: y_value}]);
						}

					}
				});
		}
	};
};
