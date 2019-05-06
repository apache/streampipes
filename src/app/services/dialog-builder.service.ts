import * as angular from 'angular';

export class DialogBuilder {

    constructor() {

    }

    // TODO: Can't resolve
    getDialogTemplate(controller, template) {
        return {
            controller: controller,
            controllerAs: "ctrl",
            bindToController: true,
            template: template,
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            //scope: this.$scope,
            //rootScope: this.$rootScope,
            //preserveScope: true
        }
    }
}
