'use strict';
spMapWidgetConfig.$inject = [];

export default function spMapWidgetConfig() {
    return {
        restrict: 'E',
        templateUrl: 'app/dashboard/templates/map/mapConfig.html',
        scope: {
            wid: '='
        }
    };
};