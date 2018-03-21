export let spTrafficlightWidgetConfig = {
    templateUrl: 'src/app/dashboard/templates/trafficlight/trafficlightConfig.html',
    bindings: {
        wid: '='
    },
    controller: class TrafficlightConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
