'use strict';
spRawWidgetConfig.$inject = [];

export default function spRawWidgetConfig() {
    return {
        restrict: 'E',
        templateUrl: 'app/dashboard/templates/raw/rawConfig.html',
        scope: {
            wid: '='
        }
    };
};