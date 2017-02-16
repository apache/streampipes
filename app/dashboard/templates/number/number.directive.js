import WidgetInstances from '../../widget-instances.service.js'
'use strict';
numberWidget.$inject = ['WidgetInstances', '$filter'];

export default function numberWidget(WidgetInstances, $filter) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'app/dashboard/templates/number/number.html',
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
