declare const require: any;

export let spRawWidgetConfig = {
    template: require('./rawConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class RawConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};