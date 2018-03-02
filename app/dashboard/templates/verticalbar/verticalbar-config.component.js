export let SpVerticalbarWidgetConfig = {
    restrict: 'E',
    templateUrl: 'app/dashboard/templates/verticalbar/verticalbarConfig.html',
    scope: {
        wid: '='
    },
    controller: class VerticalbarConfigCtrl {
        constructor() {}
    },
    controllerAs: 'ctrl'
};
