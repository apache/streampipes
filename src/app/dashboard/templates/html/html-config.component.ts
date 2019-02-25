declare const require: any;

export let spHtmlWidgetConfig = {
    template: require('./htmlConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class HtmlConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
