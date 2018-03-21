declare const require: any;

export let spImageWidgetConfig = {
    template: require('./imageConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class ImageConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
