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

import {WidgetInstances} from '../../widget-instances.service'

'use strict';
declare const require: any;
numberWidget.$inject = ['WidgetInstances', '$filter'];

export default function numberWidget(WidgetInstances, $filter) {
    return {
        restrict: 'A',
        replace: true,
        template: require('./number.html'),
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function (data) {
                $scope.selectedNumberProperty = data.visualisation.schema.selectedNumberProperty.properties.runtimeName;
                $scope.formatDate =
                    data
                        .visualisation
                        .schema
                        .selectedNumberProperty
                        .properties
                        .domainProperties
                        .indexOf('http://test.de/timestamp') > -1;
            });

            $scope.getRandomColor = function() {
                var random = Math.floor(Math.random() * 6);
                var colors = ["darkblue", "#2196F3", "#3F51B5", "#00BCD4", "#03A9F4", "#673AB7"];
                return colors[random];
            }

            $scope.isNumber = function(number) {
                return (typeof number) === "number";
            }

            $scope.color = $scope.getRandomColor();
        },
        link: function postLink(scope) {
            scope.$watch('data', function (data) {
                if (data) {
                    if (scope.formatDate) {
                        scope.item = $filter('date')(data[scope.selectedNumberProperty], 'dd.MM.yyyy HH:mm:ss');
                    } else {
                        scope.item = data[scope.selectedNumberProperty];
                    }
                }
            });
        }
    };
};
