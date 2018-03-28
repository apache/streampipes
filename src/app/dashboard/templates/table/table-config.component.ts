declare const require: any;

export let spTableWidgetConfig = {
    template: require('./tableConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class TableConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
