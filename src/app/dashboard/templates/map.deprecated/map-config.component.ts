declare const require: any;

export let spMapWidgetConfig = {
    template: require('./mapConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class MapConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};