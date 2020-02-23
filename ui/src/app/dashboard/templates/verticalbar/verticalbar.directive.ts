/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {WidgetInstances} from '../../widget-instances.service'

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
