declare const google: any;

import { WidgetInstances } from '../../widget-instances.service'
'use strict';

declare const require: any;
heatmapWidget.$inject = ['WidgetInstances', '$http', 'NgMap'];

export default function heatmapWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        template: require('./heatmap.html'),
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope, NgMap) {
            WidgetInstances.get($scope.widgetId).then(function (data) {
                $scope.widgetConfig = data.visualisation.schema.config;

                $scope.pointArray = new google.maps.MVCArray();

                NgMap.getMap().then(function (map) {
                    $scope.map = map;

                    $scope.heatmap = new google.maps.visualization.HeatmapLayer({
                        data: $scope.pointArray,
                        map: map,
                        radius: 20,
                        maxIntensity : 20

                    });
                });
            });
        },
        link: function postLink(scope) {
            scope.$watch('data', function (data) {
                if (data) {

                    if (scope.pointArray.getLength() % 1000 == 0) {
                        if (scope.pointArray.getLength() > 0) scope.pointArray.removeAt(0);
                        else {
                            scope.currentLocation = [data[scope.widgetConfig.selectedLongitudeMapping.properties.runtimeName],
                                data[scope.widgetConfig.selectedLatitudeMapping.properties.runtimeName]];
                        }
                    }

                    scope.pointArray.push(new google.maps.LatLng(data[scope.widgetConfig.selectedLatitudeMapping.properties.runtimeName],
                        data[scope.widgetConfig.selectedLongitudeMapping.properties.runtimeName]));
                }
            });
        }
    };
};