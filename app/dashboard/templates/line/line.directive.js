import Widgets from '../../widgets.service.js'
'use strict';
lineWidget.$inject = ['Widgets'];

export default function lineWidget(Widgets) {
	return {
		restrict: 'A',
		replace: true,
		templateUrl: 'app/dashboard/templates/line/line.html',
		scope: {
			data: '=',
			widgetId: '@'
		},
		controller: function ($scope) {
			$scope.widgetConfig = Widgets.get($scope.widgetId).vis.schema.config;
			$scope.myChart = null;
		},
		link: function postLink(scope, element) {
				scope.$watch('data', function (data) {
					if (data) {
						if (!scope.myChart) {
							scope.myChart = element.epoch({
								type: 'time.line',
								data: [{ label: "Series 1", values: []}],
								axes: ['bottom', 'left']
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
