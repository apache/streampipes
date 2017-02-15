'use strict';
spHeatmapWidgetConfig.$inject = [];

export default function spHeatmapWidgetConfig() {
    return {
        restrict: 'E',
        templateUrl: 'app/dashboard/templates/heatmap/heatmapConfig.html',
        scope: {
            wid: '='
        }
    };
};