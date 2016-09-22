import Widgets from '../../widgets.service.js'
'use strict';
gaugeWidget.$inject = ['Widgets'];

export default function gaugeWidget(Widgets) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'app/dashboard/templates/gauge/gauge.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            $scope.widgetConfig = Widgets.get($scope.widgetId).vis.schema.config;
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
