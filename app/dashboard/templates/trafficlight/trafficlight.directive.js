import Widgets from '../../widgets.service.js'
'use strict';
trafficlightWidget.$inject = ['Widgets'];

export default function trafficlightWidget(Widgets) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'app/dashboard/templates/trafficlight/trafficlight.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            var widgetConfig = Widgets.get($scope.widgetId);
            $scope.selectedNumberProperty = widgetConfig.vis.schema.selectedNumberProperty.properties.runtimeName;
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