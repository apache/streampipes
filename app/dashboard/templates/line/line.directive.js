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
			//selectedTimestampMapping
			//selectedNumberMapping

			//$scope.selectedNumberProperty = widgetConfig.vis.schema.selectedNumberProperty.properties.runtimeName;

			//var widgetConfig = Widgets.get($scope.widgetId);
			//$scope.selectedNumberProperty = widgetConfig.vis.schema.selectedNumberProperty.properties.runtimeName;
			//
			$scope.lineData = [];
		},
		link: function postLink(scope, element) {

			var range = [0, 100];
			var dd = [{ label: "Series 1", values: [], range: range}];

			 //var elementResult = element[0].querySelector('#lineChart');

			var myChart = element.epoch({
				type: 'time.line',
				data: dd
			});

			scope.$watch('data', function (data) {
				if (data) {
					
					var el = {time: data[scope.widgetConfig.selectedTimestampMapping.properties.runtimeName], y: data[scope.widgetConfig.selectedNumberMapping.properties.runtimeName]}
					//el.time = parseInt(el.time.toString().slice(0, -3));
					//el.time = Math.floor(el.time/1000);
					el.time = 1370044800;
					myChart.push([{ label: "Series 1", values: [el], range: range}]);
				}


				//}
			});
		}
	};
};
