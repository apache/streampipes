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
            $scope.markers = {};
            $scope.markersTimeout = {};

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
            var carIcon = L.icon({
                iconUrl: 'assets/img/pe_icons/car.png',

                iconSize:     [15, 15], // size of the icon
                shadowSize:   [0, 0], // size of the shadow
                iconAnchor:   [0, 0], // point of the icon which will correspond to marker's location
                shadowAnchor: [0, 0],  // the same for the shadow
                popupAnchor:  [0, 0] // point from which the popup should open relative to the iconAnchor
            });

            var getId = function (data) {
                return data[scope.widgetConfig.selectedLabelMapping.properties.runtimeName];
            };

            var getMarker = function(data) {


                var lat = data[scope.widgetConfig.selectedLatitudeMapping.properties.runtimeName];
                var long = data[scope.widgetConfig.selectedLongitudeMapping.properties.runtimeName];
                var text = "<h4>Id: " + getId(data) + "</h4></br>";
                for (var key in data) {;
                    text =  text.concat("<b>" +key +"</b>" +  ": " + data[key] + "<br>");
                }

                // TODO remove old makers

                var marker = L.marker([lat, long], {icon: carIcon})
                    .addTo(scope.map)
                    .bindPopup(text);

                return marker;

            };

            scope.$watch('data', function (data) {
                if (data) {

                    var marker = getMarker(data);
                    var id = getId(data);

                    if (id in scope.markers) {
                        scope.map.removeLayer(scope.markers[id]);
                    }

                    var currentDate = new Date().getTime();
                    scope.markers[id] = marker;
                    scope.markersTimeout[id] = currentDate;

                    if (scope.refocus) {
                        var group = new L.featureGroup(Object.values(scope.markers));
                        scope.map.fitBounds(group.getBounds());
                    }


                    for (var key in scope.markersTimeout) {

                        if (scope.markersTimeout[key] + 5000 < currentDate) {
                            console.log(scope.markers[key]);
                            scope.map.removeLayer(scope.markers[key]);
                        }
                    }


                    // scope.map.panTo(new L.LatLng(lat, long));

                }
            });
        },
        controllerAs: 'ctrl'
    };
};