'use strict';

spTrafficlightWidgetConfig.$inject = [];

export default function spTrafficlightWidgetConfig() {
    return {
        restrict: 'E',
        templateUrl: 'app/dashboard/templates/trafficlight/trafficlightConfig.html',
        scope: {
            wid: '='
        }
    };
};
