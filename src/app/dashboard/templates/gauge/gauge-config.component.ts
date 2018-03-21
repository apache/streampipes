export let spGaugeWidgetConfig = {
    templateUrl: 'src/app/dashboard/templates/gauge/gaugeConfig.html',
    bindings: {
        wid: '='
    },
    controller: class GaugeConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
