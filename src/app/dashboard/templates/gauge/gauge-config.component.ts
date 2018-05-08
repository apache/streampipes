declare const require: any;

export let spGaugeWidgetConfig = {
    template: require('./gaugeConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class GaugeConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
