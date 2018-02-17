'use strict';

spVerticalbarWidgetConfig.$inject = [];

export default function spVerticalbarWidgetConfig() {
    return {
        restrict: 'E',
        templateUrl: 'app/dashboard/templates/verticalbar/verticalbarConfig.html',
        scope: {
            wid: '='
        }
    };
};
