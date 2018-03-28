declare const require: any;

export let spTrafficlightWidgetConfig = {
    template: require('./trafficlightConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class TrafficlightConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
