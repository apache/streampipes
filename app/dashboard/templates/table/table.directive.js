import WidgetInstances from '../../widget-instances.service.js'

'use strict';
tableWidget.$inject = ['WidgetInstances', '$filter'];

export default function tableWidget(WidgetInstances, $filter) {
		return {
			restrict: 'A',
			replace: true,
			templateUrl: 'app/dashboard/templates/table/table.html',
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
					if (eventProperty.properties.domainProperties.indexOf('http://schema.org/DateTime')>-1) {
						return true;	
					}
					return false;
				}

				//Add the colums that where selected by the user
				var widgetConfig = WidgetInstances.get($scope.widgetId);
				$scope.columns = [];
				angular.forEach(widgetConfig.vis.schema.eventProperties, function(prop) {

					if (prop.isSelected) {
						var name = prop.properties.runtimeName;
						var column = { id: name, key: name, label: name};
						
						if (isDate(prop)) {
							column.format = formatDate;
						}

						$scope.columns.push(column);
					}
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
