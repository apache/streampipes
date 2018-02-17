'use strict';
spHeatmapWidgetConfig.$inject = [];

export default function spHeatmapWidgetConfig() {
    return {
        restrict: 'E',
        templateUrl: 'src/app/dashboard/templates/heatmap/heatmapConfig.html',
        scope: {
            wid: '='
        }
    };
};