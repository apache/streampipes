/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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