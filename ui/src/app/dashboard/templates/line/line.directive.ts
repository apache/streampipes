/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { WidgetInstances } from '../../widget-instances.service'
'use strict';
lineWidget.$inject = ['WidgetInstances'];
declare const require: any;

export default function lineWidget(WidgetInstances) {
	return {
		restrict: 'A',
		replace: true,
		template: require('./line.html'),
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
