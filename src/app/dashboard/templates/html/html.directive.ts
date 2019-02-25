import { WidgetInstances } from '../../widget-instances.service'
'use strict';
htmlWidget.$inject = ['WidgetInstances'];

export default function htmlWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: './htmltemplate.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function(data) {
                $scope.widgetConfig = data.visualisation.schema.config;
                $scope.selectedUrlMapping = data.visualisation.schema.selectedNumberProperty.properties.runtimeName;

            })

        },
        link: function postLink(scope, element) {

            scope.$watch('data', function (data) {
                if (data) {
                    scope.item = data[scope.selectedUrlMapping];
                }
            });
        }
    };
};
