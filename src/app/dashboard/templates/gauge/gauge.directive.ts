import { WidgetInstances } from '../../widget-instances.service'
'use strict';

declare const require: any;

gaugeWidget.$inject = ['WidgetInstances'];

export default function gaugeWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        template: require('./gauge.html'),
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
					WidgetInstances.get($scope.widgetId).then(function(data) {
						$scope.widgetConfig = data.visualisation.schema.config;
                        $scope.min = data.visualisation.config.min;
                        $scope.max = data.visualisation.config.max;
					})
            $scope.lineData = [];
            $scope.myChart = null;

        },
        link: function postLink(scope, element) {

            scope.$watch('data', function (data) {
                if (data) {
                    if (scope.myChart == null) {
                        scope.myChart = element.epoch({
                            type: 'time.gauge',
                            value: 0.0,
                            fps: 10,
                            format: function (v) {
                                return v.toFixed(2);
                            },
                            domain: [scope.min, scope.max]
                        });
                    } else {
                        scope.myChart.update(data[scope.widgetConfig.selectedNumberMapping.properties.runtimeName]);
                    }
                }
            });
        }
    };
};
