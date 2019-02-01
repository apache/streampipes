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
            WidgetInstances.get($scope.widgetId).then(function (data) {
                $scope.widgetConfig = data.visualisation.schema.config;
            });

            $scope.map = L.map('map', {
                center: [51.505, -0.09],
                zoom: 13
            });


            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo($scope.map);

            // WidgetInstances.get($scope.widgetId).then(function (data) {
            //     $scope.widgetConfig = data.visualisation.schema.config;
            // });
        },
        link: function postLink(scope) {
            scope.$watch('data', function (data) {
                if (data) {
                    console.log(data);
                    var lat = data[scope.widgetConfig.selectedLatitudeMapping.properties.runtimeName];
                    console.log(lat);
                    var long = data[scope.widgetConfig.selectedLongitudeMapping.properties.runtimeName];
                    console.log(long);

                    L.marker([lat, long]).addTo(scope.map);
                    // L.setView([lat, long], 13).addTo(scope.map);

                    scope.map.panTo(new L.LatLng(lat, long));

                }
            });
        }
    };
};