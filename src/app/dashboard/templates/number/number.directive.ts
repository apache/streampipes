import { WidgetInstances } from '../../widget-instances.service'
'use strict';
numberWidget.$inject = ['WidgetInstances', '$filter'];

export default function numberWidget(WidgetInstances, $filter) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'number.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function (data) {
                $scope.selectedNumberProperty = data.visualisation.schema.selectedNumberProperty.properties.runtimeName;
                $scope.formatDate =
                    data
                        .visualisation
                        .schema
                        .selectedNumberProperty
                        .properties
                        .domainProperties
                        .indexOf('http://test.de/timestamp') > -1;
            });

            $scope.getRandomColor = function() {
                var random = Math.floor(Math.random() * 6);
                var colors = ["darkblue", "#2196F3", "#3F51B5", "#00BCD4", "#03A9F4", "#673AB7"];
                return colors[random];
            }

            $scope.color = $scope.getRandomColor();
        },
        link: function postLink(scope) {
            scope.$watch('data', function (data) {
                if (data) {
                    if (scope.formatDate) {
                        scope.item = $filter('date')(data[scope.selectedNumberProperty], 'dd.MM.yyyy HH:mm:ss');
                    } else {
                        scope.item = data[scope.selectedNumberProperty];
                    }
                }
            });
        }
    };
};
