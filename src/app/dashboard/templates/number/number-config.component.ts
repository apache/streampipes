declare const require: any;

export let spNumberWidgetConfig = {
    template: require('./numberConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class NumberConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
