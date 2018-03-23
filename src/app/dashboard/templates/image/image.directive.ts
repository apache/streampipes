import { WidgetInstances } from '../../widget-instances.service'
'use strict';
imageWidget.$inject = ['WidgetInstances'];

export default function imageWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'image.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function(data) {
                $scope.widgetConfig = data.visualisation.schema.config;
                $scope.selectedImageMapping = data.visualisation.schema.selectedNumberProperty.properties.runtimeName;

            })

        },
        link: function postLink(scope, element) {

            scope.$watch('data', function (data) {
                if (data) {
                    scope.item = data[scope.selectedImageMapping];
                }
            });
        }
    };
};
