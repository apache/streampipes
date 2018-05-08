declare const require: any;

export let spVerticalbarWidgetConfig = {
    restrict: 'E',
    template: require('./verticalbarConfig.html'),
    bindings: {
        wid: '='
    },
    controller: class VerticalbarConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
