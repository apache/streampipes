import { WidgetInstances } from '../../widget-instances.service'
'use strict';
declare const require: any;
rawWidget.$inject = ['WidgetInstances'];

export default function rawWidget(WidgetInstances) {
    return {
        restrict: 'A',
        replace: true,
        template: require('./raw.html'),
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
                    scope.items = data;
                }
            });
        }
    };
};