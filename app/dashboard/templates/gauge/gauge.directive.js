import WidgetInstances from '../../widget-instances.service.js'
'use strict';
gaugeWidget.$inject = ['WidgetInstances'];

export default function gaugeWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'app/dashboard/templates/gauge/gauge.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            $scope.widgetConfig = WidgetInstances.get($scope.widgetId).vis.schema.config;
            $scope.lineData = [];
        },
        link: function postLink(scope, element) {

            var myChart = element.epoch({
                type: 'time.gauge',
                value: 0.0,
                fps: 10,
                format: function (v) {
                    return v.toFixed(2);
                },
                domain: [0, 100]
            });

            scope.$watch('data', function (data) {
                if (data) {
                    myChart.update(data[scope.widgetConfig.selectedNumberMapping.properties.runtimeName]);
                }
            });
        }
    };
};
