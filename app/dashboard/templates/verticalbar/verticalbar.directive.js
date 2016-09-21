import Widgets from '../../widgets.service.js'
'use strict';
verticalbarWidget.$inject = ['Widgets'];

export default function verticalbarWidget(Widgets) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'app/dashboard/templates/verticalbar/verticalbar.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            var widgetConfig = Widgets.get($scope.widgetId);
            $scope.selectedNumberProperty = widgetConfig.vis.schema.selectedNumberProperty.properties.runtimeName;

            // TODO replace with min/max values
            $scope.printValue = function(value) {
                return 100 - percent(0, 100, value);
            }

            var percent = function(min, max, current) {
                    return 100 * (current - min) / (max - min);
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
