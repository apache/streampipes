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

import { WidgetInstances } from '../../widget-instances.service'
import * as angular from 'angular';
'use strict';
declare const require: any;
tableWidget.$inject = ['WidgetInstances', '$filter'];

export default function tableWidget(WidgetInstances, $filter) {
	return {
		restrict: 'A',
		replace: true,
		template: require('./table.html'),
		scope: {
			data: '=',
			widgetId: '@'
		},
		controller: function ($scope) {
			$scope.tableOptions = {
				initialSorts: [
					{ id: 'value', dir: '-' }
				]
			};

			var formatDate = function(value) {
				return $filter('date')(value, 'yyyy-MM-dd HH:mm:ss');	
			}

			var isDate = function(eventProperty) {
				if (eventProperty.properties.domainProperties && eventProperty.properties.domainProperties.indexOf('http://schema.org/DateTime')>-1) {
					return true;	
				}
				return false;
			}

			$scope.columns = []
			$scope.ready = false;

			//Add the colums that where selected by the user
			WidgetInstances.get($scope.widgetId).then(function(widgetConfig) {
				angular.forEach(widgetConfig.visualisation.schema.eventProperties, function(prop) {

					if (prop.isSelected) {
						var name = prop.properties.runtimeName;
						var column = { id: name, key: name, label: name};

						if (isDate(prop)) {
							column['format'] = formatDate;
						}

						$scope.columns.push(column);
						$scope.ready = true;
					}
				});

			});

		},
		link: function postLink(scope) {
			scope.$watch('data', function (data) {
				if (data) {
					scope.items = data;
				}
			});
		}
	};
};
