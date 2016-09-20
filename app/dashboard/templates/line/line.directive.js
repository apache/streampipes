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
			//var widgetConfig = Widgets.get($scope.widgetId);
			//$scope.selectedNumberProperty = widgetConfig.vis.schema.selectedNumberProperty.properties.runtimeName;
		},
		link: function postLink(scope, element) {
			element.epoch({
				type: 'time.line',
				data: [
					{
						label: "Series 1",
						values: [ {x: 0, y: 0}, {x: 10, y: 10} , {x: 20, y: 20}, {x: 30, y: 30}, {x: 40, y: 40}, {x: 50, y: 50}, {x: 60, y: 60}, {x: 70, y: 70}]
					}
				]
			});
			//scope.$watch('data', function (data) {
			//if (data) {
			//scope.item = data;
			//}
			//});
		}
	};
};
