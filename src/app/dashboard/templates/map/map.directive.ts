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
            $scope.refocus = true;
            $scope.markers = [];

            // $scope.mapId = "map_" + Math.floor(Math.random() * 9999);
            WidgetInstances.get($scope.widgetId).then(function (data) {
                $scope.widgetConfig = data.visualisation.schema.config;
            });

            $scope.map = L.map("map", {
                center: [51.505, -0.09],
                zoom: 13
            });

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo($scope.map);

            this.toggleRefocus = function () {
               console.log("Toggle")
                $scope.refocus = !$scope.refocus;
            }
            // WidgetInstances.get($scope.widgetId).then(function (data) {
            //     $scope.widgetConfig = data.visualisation.schema.config;
            // });
        },
        link: function postLink(scope) {

            scope.$watch('data', function (data) {
                if (data) {

                    var lat = data[scope.widgetConfig.selectedLatitudeMapping.properties.runtimeName];
                    var long = data[scope.widgetConfig.selectedLongitudeMapping.properties.runtimeName];
                    var text = "<h4>Id: " + data[scope.widgetConfig.selectedLabelMapping.properties.runtimeName] + "</h4></br>";
                    for (var key in data) {;
                        text =  text.concat("<b>" +key +"</b>" +  ": " + data[key] + "<br>");
                    }

                    var marker = L.marker([lat, long])
                        .addTo(scope.map)
                        .bindPopup(text);

                    scope.markers.push(marker);

                    if (scope.refocus) {
                        var group = new L.featureGroup(scope.markers);
                        scope.map.fitBounds(group.getBounds());
                    }

                    scope.map.panTo(new L.LatLng(lat, long));

                }
            });
        },
        controllerAs: 'ctrl'
    };
};