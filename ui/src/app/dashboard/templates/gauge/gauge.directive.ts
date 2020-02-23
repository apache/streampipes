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

gaugeWidget.$inject = ['WidgetInstances'];

export default function gaugeWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        template: require('./gauge.html'),
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
					WidgetInstances.get($scope.widgetId).then(function(data) {
						$scope.widgetConfig = data.visualisation.schema.config;
                        $scope.min = data.visualisation.config.min;
                        $scope.max = data.visualisation.config.max;
					})
            $scope.lineData = [];
            $scope.myChart = null;

        },
        link: function postLink(scope, element) {

            scope.$watch('data', function (data) {
                if (data) {
                    if (scope.myChart == null) {
                        scope.myChart = element.epoch({
                            type: 'time.gauge',
                            value: 0.0,
                            fps: 10,
                            format: function (v) {
                                return v.toFixed(2);
                            },
                            domain: [scope.min, scope.max]
                        });
                    } else {
                        scope.myChart.update(data[scope.widgetConfig.selectedNumberMapping.properties.runtimeName]);
                    }
                }
            });
        }
    };
};
