import WidgetInstances from '../../widget-instances.service.js'
'use strict';
mapWidget.$inject = ['WidgetInstances', 'NgMap'];

export default function mapWidget(WidgetInstances, NgMap) {
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
            });
        },
        link: function postLink(scope) {
            scope.$watch('data', function (data) {
                if (data) {
                    NgMap.getMap().then(function(map) {
                        console.log(map);
                    });
                    console.log("show");
                    scope.items = data;
                    console.log(scope.items);
                    scope.currentLocation = [data[scope.widgetConfig.selectedLongitudeMapping.properties.runtimeName],
                        data[scope.widgetConfig.selectedLatitudeMapping.properties.runtimeName]];
                    console.log(scope.currentLocation);
                }
            });
        }
    };
};