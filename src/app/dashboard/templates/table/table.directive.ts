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
