import { WidgetInstances } from '../../widget-instances.service'
'use strict';
trafficlightWidget.$inject = ['WidgetInstances'];

export default function trafficlightWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'trafficlight.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function(data) {
                $scope.selectedNumberProperty = data.visualisation.schema.selectedNumberProperty.properties.runtimeName;
                $scope.criticalThresholdOperator = data.visualisation.config.critical.operator;
                $scope.criticalThresholdValue = data.visualisation.config.critical.value;
                $scope.criticalThresholdRange = data.visualisation.config.critical.range;
            });

            $scope.active = function(id, value) {
                if ((exceedsThreshold(value) && id == 0) ||
                    (isInWarningRange(value) && id == 1) ||
                    (isInOkRange(value) && id == 2)) {
                    return "active";
                } else return "";
            }

            var exceedsThreshold = function(value) {
                if ($scope.criticalThresholdOperator == 'ge') {
                    return value >= $scope.criticalThresholdValue;
                } else {
                    return value <= $scope.criticalThresholdValue;
                }
            }

            var isInWarningRange = function(value) {
                if (exceedsThreshold(value)) return false;
                else {
                    if ($scope.criticalThresholdOperator == 'ge') {
                        return value >= ($scope.criticalThresholdValue - $scope.criticalThresholdValue*($scope.criticalThresholdRange/100));
                    } else {
                        return value <= ($scope.criticalThresholdValue + $scope.criticalThresholdValue*($scope.criticalThresholdRange/100));
                    }
                }
            }
            var isInOkRange = function(value) {
                return !exceedsThreshold(value) && !isInWarningRange(value);
            }
        },
        link: function postLink(scope) {
            scope.$watch('data', function (data) {
                if (data) {
                    scope.item = data;
                }
            });
        }
    };
};
