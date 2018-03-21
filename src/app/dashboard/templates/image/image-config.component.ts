declare const require: any;

export let spImageWidgetConfig = {
    templateUrl: require('./imageConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class ImageConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
