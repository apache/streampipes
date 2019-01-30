import { WidgetInstances } from '../../widget-instances.service'
'use strict';
mapWidget.$inject = ['WidgetInstances', 'NgMap'];
declare let L;

export default function mapWidget(WidgetInstances, NgMap) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'map.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            var map = L.map('map', {
                center: [51.505, -0.09],
                zoom: 13
            });


            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo(map);

            L.marker([51.505, -0.09]).addTo(map);
            // WidgetInstances.get($scope.widgetId).then(function (data) {
            //     $scope.widgetConfig = data.visualisation.schema.config;
            // });
        },
        link: function postLink(scope) {
            // scope.$watch('data', function (data) {
            //     if (data) {
            //         scope.currentLocation = [data[scope.widgetConfig.selectedLongitudeMapping.properties.runtimeName],
            //             data[scope.widgetConfig.selectedLatitudeMapping.properties.runtimeName]];
            //     }
            // });
        }
    };
};