export let spVerticalbarWidgetConfig = {
    restrict: 'E',
    templateUrl: 'src/app/dashboard/templates/verticalbar/verticalbarConfig.html',
    bindings: {
        wid: '='
    },
    controller: class VerticalbarConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
