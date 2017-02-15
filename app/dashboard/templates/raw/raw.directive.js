import WidgetInstances from '../../widget-instances.service.js'
'use strict';
rawWidget.$inject = ['WidgetInstances'];

export default function rawWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'app/dashboard/templates/raw/raw.html',
        scope: {
            data: '=',
            widgetId: '@'
        },
        controller: function ($scope) {
            WidgetInstances.get($scope.widgetId).then(function(data) {
            });
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