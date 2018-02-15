export class DialogBuilder {

    constructor() {

    }

    getDialogTemplate(controller, templateUrl) {
        return {
            controller: controller,
            controllerAs: "ctrl",
            bindToController: true,
            templateUrl: templateUrl,
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            //scope: this.$scope,
            //rootScope: this.$rootScope,
            //preserveScope: true
        }
    }
}
