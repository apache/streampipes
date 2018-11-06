declare const require: any;

export let spLineWidgetConfig = {
    template: require('./lineConfig.html'),
    bindings: {
        wid: '=',
        configForm: "="
    },
    controller: class LineConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
