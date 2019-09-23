/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { WidgetInstances } from '../../widget-instances.service'
'use strict';
imageWidget.$inject = ['WidgetInstances'];
declare const require: any;

export default function imageWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        template: require('./image.html'),
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function(data) {
                $scope.widgetConfig = data.visualisation.schema.config;
                $scope.selectedImageMapping = data.visualisation.schema.selectedNumberProperty.properties.runtimeName;

            })

        },
        link: function postLink(scope, element) {

            scope.$watch('data', function (data) {
                if (data) {
                    scope.item = data[scope.selectedImageMapping];
                }
            });
        }
    };
};
