declare const require: any;

export let spHeatmapWidgetConfig = {
    template: require('./heatmapConfig.html'),
    bindings: {
            wid: '='
    },
    controller: class HeatmapConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};