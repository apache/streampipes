import WidgetInstances from '../../widget-instances.service.js'
'use strict';
mapWidget.$inject = ['WidgetInstances'];

export default function mapWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'app/dashboard/templates/map/map.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function (data) {
                $scope.widgetConfig = data.visualisation.schema.config;
                $scope.map = { center: { latitude: 45, longitude: -73 }, zoom: 8 };

            });

        },
        link: function postLink(scope, element) {
            scope.$watch('data', function (data) {
                if (data) {
                    scope.show = true;
                    console.log("show");
                    scope.items = data;
                }
            });
        }
    };
};