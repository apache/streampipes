export let spVerticalbarWidgetConfig = {
    restrict: 'E',
    templateUrl: 'app/dashboard/templates/verticalbar/verticalbarConfig.html',
    bindings: {
        wid: '='
    },
    controller: class VerticalbarConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
